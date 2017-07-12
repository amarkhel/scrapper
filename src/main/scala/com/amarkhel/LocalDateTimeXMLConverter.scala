package com.amarkhel

import java.time.LocalDateTime;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

class LocalDateTimeXMLConverter extends AbstractSingleValueConverter {

  override def toString(source: Any) = source.toString

  override def fromString(str: String) = LocalDateTime.parse(str)

  def canConvert(clazz: Class[_]) = clazz != null && clazz.getName == "java.time.LocalDateTime"
}