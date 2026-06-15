package com.example.dentalclinic.data.fake

import com.example.dentalclinic.data.AppSettings
import com.example.dentalclinic.data.model.*

object FakeDentalData {
    private val isAr get() = AppSettings.currentLanguage == "ar"

    val patient: Patient
        get() = if (isAr) {
            Patient(
                name = "جون دو",
                age = 34,
                phone = "+1 555 0124",
                email = "john.doe@email.com",
                emergencyContact = "سارة دو - +1 555 0188",
                insurance = "برايت كير بريميوم"
            )
        } else {
            Patient(
                name = "John Doe",
                age = 34,
                phone = "+1 555 0124",
                email = "john.doe@email.com",
                emergencyContact = "Sarah Doe - +1 555 0188",
                insurance = "BrightCare Premium"
            )
        }

    val appointments: List<Appointment>
        get() = if (isAr) {
            listOf(
                Appointment("فحص دوري", "25 مايو 2026", "10:00 صباحاً", "د. سارة جونسون", "مؤكد"),
                Appointment("جلسة تبييض", "02 يونيو 2026", "01:30 مساءً", "د. مارك كارتر", "قادم"),
                Appointment("متابعة قناة الجذر", "18 أبريل 2026", "09:15 صباحاً", "د. سارة جونسون", "مكتمل")
            )
        } else {
            listOf(
                Appointment("Regular Checkup", "May 25, 2026", "10:00 AM", "Dr. Sarah Johnson", "Confirmed"),
                Appointment("Whitening Session", "Jun 02, 2026", "01:30 PM", "Dr. Mark Carter", "Upcoming"),
                Appointment("Root Canal Follow-up", "Apr 18, 2026", "09:15 AM", "Dr. Sarah Johnson", "Completed")
            )
        }

    val dentists: List<Dentist>
        get() = if (isAr) {
            listOf(
                Dentist("د. سارة جونسون", "طبيب أسنان تجميلي", "4.9"),
                Dentist("د. مارك كارتر", "طبيب تقويم أسنان", "4.8"),
                Dentist("د. لينا أحمد", "جراح فم", "4.7")
            )
        } else {
            listOf(
                Dentist("Dr. Sarah Johnson", "Cosmetic Dentist", "4.9"),
                Dentist("Dr. Mark Carter", "Orthodontist", "4.8"),
                Dentist("Dr. Lina Ahmed", "Oral Surgeon", "4.7")
            )
        }

    val xRays: List<XRayRecord>
        get() = if (isAr) {
            listOf(
                XRayRecord("أشعة سينية بانورامية", "18 مايو 2026", "لم يتم اكتشاف فقدان عظمي. لوحة خفيفة بالقرب من الأضراس."),
                XRayRecord("فحص أجنحة العضة", "04 أبريل 2026", "تغير مبكر في المينا في الضاحك العلوي الأيمن."),
                XRayRecord("فحص حول الذروة", "12 مارس 2026", "منطقة قناة الجذر تلتئم بشكل طبيعي.")
            )
        } else {
            listOf(
                XRayRecord("Panoramic X-Ray", "May 18, 2026", "No bone loss detected. Mild plaque near molars."),
                XRayRecord("Bitewing Scan", "Apr 04, 2026", "Early enamel change on upper right premolar."),
                XRayRecord("Periapical Scan", "Mar 12, 2026", "Root canal area healing normally.")
            )
        }

    val diagnoses: List<Diagnosis>
        get() = if (isAr) {
            listOf(
                Diagnosis("سن 14", "تسوس مبكر", "متوسط", "حشوة مركبة وعناية بالفلورايد."),
                Diagnosis("سن 30", "حساسية اللثة", "منخفض", "تنظيف عميق وروتين فرشاة ناعمة."),
                Diagnosis("سن 8", "بقع تجميلية", "منخفض", "جلسة تبييض بعد الفحص.")
            )
        } else {
            listOf(
                Diagnosis("Tooth 14", "Early cavity", "Moderate", "Composite filling and fluoride care."),
                Diagnosis("Tooth 30", "Gum sensitivity", "Low", "Deep cleaning and soft brush routine."),
                Diagnosis("Tooth 8", "Cosmetic stain", "Low", "Whitening session after checkup.")
            )
        }

    val history: List<MedicalHistoryItem>
        get() = if (isAr) {
            listOf(
                MedicalHistoryItem("تنظيف الأسنان", "10 مايو 2026", "تنظيف روتيني وتلميع وفحص اللثة."),
                MedicalHistoryItem("حشوة تسوس", "21 فبراير 2026", "تم وضع حشوة مركبة للسن 14."),
                MedicalHistoryItem("مراجعة الأشعة", "15 يناير 2026", "اكتملت المراجعة السنوية للأشعة.")
            )
        } else {
            listOf(
                MedicalHistoryItem("Dental Cleaning", "May 10, 2026", "Routine cleaning, polishing, and gum inspection."),
                MedicalHistoryItem("Cavity Filling", "Feb 21, 2026", "Composite filling applied to tooth 14."),
                MedicalHistoryItem("X-Ray Review", "Jan 15, 2026", "Annual radiology review completed.")
            )
        }

    val notifications: List<NotificationItem>
        get() = if (isAr) {
            listOf(
                NotificationItem("تذكير بالموعد", "موعد فحصك الدوري مجدول في 25 مايو الساعة 10:00 صباحاً.", "12 دقيقة", true),
                NotificationItem("تقرير الأشعة جاهز", "تحليل الأشعة الأخير الخاص بك متاح.", "1 ساعة", true),
                NotificationItem("تم تأكيد الدفع", "تم دفع الفاتورة رقم 2140 بنجاح.", "أمس", false)
            )
        } else {
            listOf(
                NotificationItem("Appointment Reminder", "Your regular checkup is scheduled for May 25 at 10:00 AM.", "12 min", true),
                NotificationItem("X-Ray Report Ready", "Your latest X-Ray analysis is available.", "1 hr", true),
                NotificationItem("Payment Confirmed", "Invoice #2140 has been paid successfully.", "Yesterday", false)
            )
        }

    val messages: List<ChatMessage>
        get() = if (isAr) {
            listOf(
                ChatMessage("العيادة", "مرحباً جون، تقرير الأشعة الخاص بك جاهز للمراجعة.", "09:10", false),
                ChatMessage("جون", "رائع، هل يمكنني مناقشته خلال موعدي؟", "09:12", true),
                ChatMessage("العيادة", "نعم، ستقوم د. سارة بمراجعته معك.", "09:13", false)
            )
        } else {
            listOf(
                ChatMessage("Clinic", "Hi John, your X-Ray report is ready for review.", "09:10", false),
                ChatMessage("John", "Great, can I discuss it during my appointment?", "09:12", true),
                ChatMessage("Clinic", "Yes, Dr. Sarah will review it with you.", "09:13", false)
            )
        }
}
