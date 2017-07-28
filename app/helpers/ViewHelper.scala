package helpers

/** Helper functions for views. */
object ViewHelper {
  /**
    * Takes a camel cased identifier name and returns a space separated name.
    * Example:
    * camelCaseToSpaces("thisIsA1Test") == "this is a 1 test"
    */
  def camelCaseToSpaces(name: String): String =
    "[A-Z\\d]".r.replaceAllIn(name, { m => " " + m.group(0).toLowerCase() })
}