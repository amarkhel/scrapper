package webscrapper.service

class StoppedException(message: String = null, cause: Throwable = null) extends
  RuntimeException(StoppedException.defaultMessage(message, cause), cause)

object StoppedException {
  def defaultMessage(message: String, cause: Throwable) =
    if (message != null) message
    else if (cause != null) cause.toString()
    else null
}