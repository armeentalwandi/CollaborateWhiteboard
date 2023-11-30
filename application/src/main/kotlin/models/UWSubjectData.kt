package models
import kotlinx.serialization.Serializable

@Serializable
data class UWSubjectData(
    val code: String,
    val name: String,
    val descriptionAbbreviated: String,
    val description: String,
    val associatedAcademicOrgCode: String
)
