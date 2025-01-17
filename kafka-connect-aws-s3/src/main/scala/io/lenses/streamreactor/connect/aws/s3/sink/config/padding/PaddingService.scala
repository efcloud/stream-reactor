/*
 * Copyright 2017-2023 Lenses.io Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.lenses.streamreactor.connect.aws.s3.sink.config.padding

import cats.implicits.catsSyntaxEitherId
import io.lenses.streamreactor.connect.aws.s3.config.kcqlprops.S3PropsKeyEnum.PaddingCharacter
import io.lenses.streamreactor.connect.aws.s3.config.kcqlprops.S3PropsKeyEnum.PaddingLength
import io.lenses.streamreactor.connect.aws.s3.config.kcqlprops.S3PropsKeyEnum.PaddingSelection
import io.lenses.streamreactor.connect.aws.s3.config.kcqlprops.S3PropsKeyEntry
import io.lenses.streamreactor.connect.aws.s3.config.kcqlprops.S3PropsKeyEnum
import io.lenses.streamreactor.connect.aws.s3.sink.NoOpPaddingStrategy
import io.lenses.streamreactor.connect.aws.s3.sink.PaddingStrategy
import io.lenses.streamreactor.connect.aws.s3.sink.config.S3SinkConfigDefBuilder
import io.lenses.streamreactor.connect.aws.s3.sink.config.padding.PaddingType.LeftPad
import io.lenses.streamreactor.connect.config.kcqlprops.KcqlProperties
import io.lenses.streamreactor.connect.config.kcqlprops.KcqlProperties.stringToInt
import io.lenses.streamreactor.connect.config.kcqlprops.KcqlProperties.stringToString

object PaddingService {

  val DefaultPaddingStrategy: PaddingType = LeftPad
  val DefaultPadLength:       Int         = 12
  val DefaultPadChar:         Char        = '0'
  val DefaultPadFields:       Set[String] = Set("offset")

  private trait PaddingConfigDetector[C] {

    def configApplied(config: C): Boolean

    def processConfig(config: C): PaddingService

  }

  private object ConfigDefPaddingConfigDetector extends PaddingConfigDetector[S3SinkConfigDefBuilder] {
    override def configApplied(config: S3SinkConfigDefBuilder): Boolean = config.getPaddingStrategy.nonEmpty

    override def processConfig(config: S3SinkConfigDefBuilder): PaddingService =
      config.getPaddingStrategy.map(ps => new PaddingService(Map("offset" -> ps))).get
  }

  private object KcqlPropsPaddingConfigDetector
      extends PaddingConfigDetector[KcqlProperties[S3PropsKeyEntry, S3PropsKeyEnum.type]] {
    override def configApplied(config: KcqlProperties[S3PropsKeyEntry, S3PropsKeyEnum.type]): Boolean =
      config.containsKeyStartingWith("padding.")

    override def processConfig(config: KcqlProperties[S3PropsKeyEntry, S3PropsKeyEnum.type]): PaddingService =
      fromDefaults(
        config.getOptionalChar(PaddingCharacter),
        config.getOptionalMap[String, Int](PaddingLength, stringToString, stringToInt),
        config.getEnumValue[PaddingType, PaddingType.type](PaddingType, PaddingSelection),
      )

    private def fromDefaults(
      maybeChar:   Option[Char],
      maybeFields: Option[Map[String, Int]],
      maybeType:   Option[PaddingType],
    ): PaddingService = {
      val fields: Map[String, Int] = maybeFields.getOrElse(DefaultPadFields.map(f => f -> DefaultPadLength)).toMap
      val pChar:  Char             = maybeChar.getOrElse(DefaultPadChar)
      val pType:  PaddingType      = maybeType.getOrElse(DefaultPaddingStrategy)
      val paddingStrategies = fields.map(f => f._1 -> pType.toPaddingStrategy(f._2, pChar))
      new PaddingService(paddingStrategies)
    }

  }
  def fromConfig(
    confDef: S3SinkConfigDefBuilder,
    props:   KcqlProperties[S3PropsKeyEntry, S3PropsKeyEnum.type],
  ): Either[Throwable, PaddingService] = {
    val cdConf = ConfigDefPaddingConfigDetector.configApplied(confDef)
    val kpConf = KcqlPropsPaddingConfigDetector.configApplied(props)
    (cdConf, kpConf) match {
      case (true, true) =>
        new IllegalStateException(
          "Unable to process both padding Kafka Connect config properties and KCQL properties.  Please use one or the other.  We recommend KCQL properties for additional configuration ability.",
        ).asLeft
      case (true, false) => ConfigDefPaddingConfigDetector.processConfig(confDef).asRight
      case _             => KcqlPropsPaddingConfigDetector.processConfig(props).asRight
    }

  }

}

class PaddingService(
  fields: Map[String, PaddingStrategy],
) {

  def padderFor(field: String): PaddingStrategy =
    fields.get(field) match {
      case Some(fieldPaddingStrategy) => fieldPaddingStrategy
      case None                       => NoOpPaddingStrategy
    }
}
