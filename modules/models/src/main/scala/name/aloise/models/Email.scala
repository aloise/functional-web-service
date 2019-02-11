package name.aloise.models

import java.util.regex.Pattern

import eu.timepit.refined.api.Refined

case class MatchesEmail()

object MatchesEmail {
  // I don't want to use the RFC822 compatible one here
  private val regex = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)

  import eu.timepit.refined.api.Validate

  implicit val emailValidate: Validate[String, MatchesEmail] =
    Validate.fromPredicate(
      str => regex.matcher(str).matches(),
      s"$str is not a value email"str =>,
      MatchesEmail()
    )
}

final case class Email(value: String Refined MatchesEmail)