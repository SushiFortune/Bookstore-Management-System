/**
 * BadIsbnException class that targets all errors in relation to an invalid 10 or 13 digits ISBN value, except those caught by BadIsbn10Exception and BadIsbn13Exception classes,
 * for example, BadIsbnException would target the following error: a letter being included in the ISBN value
 *  @authors Yihuan Liu Rania Maoukout
 * @version 1.0
 */
public class BadIsbnException extends Exception{
}
