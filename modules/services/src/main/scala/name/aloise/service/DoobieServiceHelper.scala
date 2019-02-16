package name.aloise.service

import doobie.util.Meta
import name.aloise.models.{Id, UserId}

trait DoobieServiceHelper {
  private implicit val doobieUserIdMeta: Meta[UserId] = Meta[Int]
    .imap(UserId)(_.value)
}
