package models
import kotlinx.serialization.Serializable

@Serializable
data class UWCourseData(
    val courseId: String,
    val courseOfferNumber: Int,
    val termCode: String,
    val termName: String,
    val associatedAcademicCareer: String,
    val associatedAcademicGroupCode: String,
    val associatedAcademicOrgCode: String,
    val subjectCode: String,
    val catalogNumber: String,
    val title: String,
    val descriptionAbbreviated: String,
    val description: String,
    val gradingBasis: String,
    val courseComponentCode: String,
    val enrollConsentCode: String,
    val enrollConsentDescription: String,
    val dropConsentCode: String,
    val dropConsentDescription: String,
    val requirementsDescription: String?
)
