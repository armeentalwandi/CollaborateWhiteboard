package models
import kotlinx.serialization.Serializable

@Serializable
data class UWTermData(
    val termCode: String,
    val name: String,
    val nameShort: String,
    val termBeginDate: String,
    val termEndDate: String,
    val sixtyPercentCompleteDate: String,
    val associatedAcademicYear: Int
)
