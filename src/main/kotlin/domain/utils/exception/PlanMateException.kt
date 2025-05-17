package domain.utils.exception

open class PlanMateException(message: String) : Exception(message) {

    class ItemNotFoundException(message: String = "Item not found.") : PlanMateException(message)

    class InvalidFormatException(message: String = "Invalid data format.") : PlanMateException(message)

    class ValidationException(message: String = "Validation failed.") : PlanMateException(message)

    class HashingException(message: String = "Failed to hash") : PlanMateException(message)

    class UserActionNotAllowedException(
        message: String = "This user is not allowed to perform this action."
    ) : PlanMateException(message)

    class InvalidStateIdException(message: String = "Invalid state id, no audit logs found.") :
        PlanMateException(message)

}