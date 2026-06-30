package com.example.dentalclinic.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dentalclinic.R
import com.example.dentalclinic.data.AppSettings
import com.example.dentalclinic.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class ChatMessage(val text: String, val isFromMe: Boolean, val time: String = "")

data class SuggestedSection(val label: String, val arLabel: String, val emoji: String, val route: String)

private val allSections = listOf(
    SuggestedSection("Appointments", "المواعيد", "📅", "appointments"),
    SuggestedSection("X-Ray", "الأشعة", "🦷", "xray"),
    SuggestedSection("Diagnosis", "التشخيص", "📋", "diagnosis"),
    SuggestedSection("History", "السجل", "📁", "history"),
)

private fun generateSmartResponse(userMessage: String): String {
    val msg = userMessage.lowercase().trim()
    val patient = AppSettings.loggedInPatient
    val patientName = patient?.fullName ?: patient?.firstName ?: (if (containsArabic(msg)) "عزيزي" else "there")

    // Language detection
    val isUserArabic = containsArabic(msg)

    // Greetings
    val enGreetings = listOf("hello", "hi", "hey", "good morning", "good afternoon", "good evening", "hi there", "hello there", "howdy")
    val arGreetings = listOf("مرحبا", "اهلا", "السلام عليكم", "صباح الخير", "مساء الخير", "هلا")
    
    if (enGreetings.any { msg.startsWith(it) || msg == it } || arGreetings.any { msg.contains(it) }) {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return if (isUserArabic) {
            val timeGreeting = when {
                hour < 12 -> "صباح الخير"
                hour < 17 -> "مساء الخير"
                else -> "مساء الخير"
            }
            "$timeGreeting، $patientName! أتمنى أن تكون بخير اليوم. 😊\n\nأنا مساعدك الطبي للأسنان. كيف يمكنني مساعدتك؟ يمكنك سؤالي عن:\n📅 حجز أو إدارة المواعيد\n🦷 عرض صور الأشعة الخاصة بك\n📋 التحقق من تقارير التشخيص\n📁 مراجعة تاريخك الطبي\n⚙️ إعدادات التطبيق وتفضيلاتك"
        } else {
            val timeGreeting = when {
                hour < 12 -> "Good morning"
                hour < 17 -> "Good afternoon"
                else -> "Good evening"
            }
            "$timeGreeting, $patientName! I hope you're doing well today. 😊\n\nI'm your dental care assistant. How can I help you? You can ask me about:\n📅 Booking or managing appointments\n🦷 Viewing your X-Ray scans\n📋 Checking diagnosis reports\n📁 Reviewing your medical history\n⚙️ App settings and preferences"
        }
    }

    // How are you
    if (msg.contains("how are you") || msg.contains("how are u") || msg.contains("how's it going") || msg.contains("كيف حالك") || msg.contains("شلونك")) {
        return if (isUserArabic) {
            "أنا بخير، شكراً لسؤالك! 😊 أنا هنا لمساعدتك في كل ما يتعلق بالعناية بأسنانك. كيف يمكنني مساعدتك اليوم؟"
        } else {
            "I'm doing great, thank you for asking! 😊 I'm here to help you with everything related to your dental care. What can I assist you with today?"
        }
    }

    // Thank you
    if (msg.contains("thank") || msg.contains("thanks") || msg.contains("شكرا") || msg.contains("شكراً") || msg.contains("تسلم")) {
        return if (isUserArabic) {
            "على الرحب والسعة، $patientName! 🎉 يسعدني دائماً تقديم المساعدة. إذا احتجت لأي شيء آخر، فأنا هنا. نتمنى لك صحة أسنان ممتازة! 🦷"
        } else {
            "You're welcome, $patientName! 🎉 It's my pleasure to help. If you ever need anything else, just let me know. Wishing you excellent dental health! 🦷"
        }
    }

    // Goodbye
    if (msg.contains("bye") || msg.contains("goodbye") || msg.contains("see you") || msg.contains("مع السلامة") || msg.contains("وداعا") || msg.contains("في أمان الله")) {
        return if (isUserArabic) {
            "في أمان الله، $patientName! 👋 تذكر تنظيف أسنانك مرتين يومياً واستخدام الخيط بانتظام. إذا احتجت للمساعدة، فأنا على بعد رسالة. أتمنى لك يوماً رائعاً!"
        } else {
            "Take care, $patientName! 👋 Remember to brush twice a day and floss regularly. If you ever need help, I'm just a message away. Have a wonderful day!"
        }
    }

    // Appointments
    if (msg.contains("appointment") || msg.contains("book") || msg.contains("schedule") || msg.contains("reserve") ||
        msg.contains("حجز") || msg.contains("موعد") || msg.contains("جدولة")) {
        return if (isUserArabic) {
            "يسعدني مساعدتك في المواعيد! 📅\n\nيمكنك عرض مواعيدك القادمة، أو حجز موعد جديد، أو إعادة جدولة زيارة حالية بالذهاب إلى قسم **المواعيد**. فقط اضغط على الزر أدناه للبدء!\n\n👉 اذهب إلى المواعيد لرؤية الأوقات المتاحة."
        } else {
            "I'd be happy to help you with appointments! 📅\n\nYou can view your upcoming appointments, book a new one, or reschedule an existing visit by going to the **Appointments** section. Just tap the button below to get started!\n\n👉 Go to Appointments to see available time slots."
        }
    }

    // X-Ray
    if (msg.contains("x-ray") || msg.contains("xray") || msg.contains("x ray") || msg.contains("scan") || msg.contains("اشعة") ||
        msg.contains("أشعة") || msg.contains("ray") || msg.contains("radiograph") || msg.contains("تصوير")) {
        return if (isUserArabic) {
            "صور الأشعة الخاصة بك متاحة في قسم **الأشعة**! 🦷\n\nيمكنك عرض صور أسنانك مع تحليل مدعوم بالذكاء الاصطناعي يبرز المناطق المهمة. تتضمن كل صورة نتائج مفصلة من طبيبك.\n\n👉 افتح قسم الأشعة لرؤية صورك وتحليلاتك."
        } else {
            "Your X-Ray scans are available in the **X-Ray** section! 🦷\n\nYou can view your dental scans along with AI-powered analysis that highlights areas of interest. Each scan includes detailed findings from your doctor.\n\n👉 Open the X-Ray section to see your images and analysis."
        }
    }

    // Diagnosis
    if (msg.contains("diagnosis") || msg.contains("diagnose") || msg.contains("result") || msg.contains("treatment") ||
        msg.contains("تشخيص") || msg.contains("نتيجة") || msg.contains("علاج") || msg.contains("report") || msg.contains("تقرير")) {
        return if (isUserArabic) {
            "تقارير التشخيص الخاصة بك متاحة في قسم **التشخيص**! 📋\n\nستجد تقارير مفصلة من طبيبك تشمل خطط العلاج والنتائج والتوصيات. يوفر تحليل الذكاء الاصطناعي رؤى إضافية.\n\n👉 اذهب إلى التشخيص لمراجعة تقاريرك."
        } else {
            "Your diagnosis reports are available in the **Diagnosis** section! 📋\n\nYou'll find detailed reports from your doctor including treatment plans, findings, and recommendations. The AI analysis provides additional insights.\n\n👉 Go to Diagnosis to review your reports."
        }
    }

    // Medical History
    if (msg.contains("history") || msg.contains("medical history") || msg.contains("record") || msg.contains("past") ||
        msg.contains("تاريخ") || msg.contains("سجل") || msg.contains("قديم") || msg.contains("سابق")) {
        return if (isUserArabic) {
            "تاريخك الطبي الكامل متاح في قسم **التاريخ الطبي**! 📁\n\nيتضمن جميع سجلاتك السابقة، صور الأشعة، تقارير التشخيص، ومعلومات المريض — كلها في مكان واحد للرجوع إليها بسهولة.\n\n👉 افتح التاريخ الطبي لمراجعة كل شيء."
        } else {
            "Your complete medical history is available in the **Medical History** section! 📁\n\nIt includes all your past records, X-Ray scans, diagnosis reports, and patient information — all in one place for easy reference.\n\n👉 Open Medical History to review everything."
        }
    }

    // Settings
    if (msg.contains("setting") || msg.contains("language") || msg.contains("dark mode") || msg.contains("theme") ||
        msg.contains("اعدادات") || msg.contains("إعدادات") || msg.contains("لغة") || msg.contains("وضع") || msg.contains("نمط")) {
        return if (isUserArabic) {
            "يمكنك إدارة جميع تفضيلات التطبيق في قسم **الإعدادات**! ⚙️\n\nمن هناك يمكنك:\n🌗 تبديل الوضع الداكن\n🌐 التبديل بين اللغتين الإنجليزية والعربية\n🔔 إدارة التنبيهات\nوالمزيد!\n\n👉 اذهب إلى الإعدادات لتخصيص تجربتك."
        } else {
            "You can manage all your app preferences in the **Settings** section! ⚙️\n\nFrom there you can:\n🌗 Toggle Dark Mode\n🌐 Switch between English and Arabic\n🔔 Manage notifications\nAnd more!\n\n👉 Go to Settings to customize your experience."
        }
    }

    // Pain / Emergency
    if (msg.contains("pain") || msg.contains("hurt") || msg.contains("ache") || msg.contains("emergency") ||
        msg.contains("ألم") || msg.contains("وجع") || msg.contains("طوارئ") || msg.contains("يؤلم")) {
        return if (isUserArabic) {
            "يؤسفني سماع أنك تشعر بالألم! 😟 بينما يمكنني تقديم إرشادات عامة، للحصول على عناية طبية مناسبة أوصي بـ:\n\n1️⃣ **حجز موعد عاجل** في قسم المواعيد\n2️⃣ إذا كانت الحالة طارئة وشديدة، يرجى زيارة أقرب عيادة أسنان أو مستشفى فوراً\n\nصحتك تأتي أولاً! 🏥"
        } else {
            "I'm sorry to hear you're in pain! 😟 While I can provide general guidance, for proper medical attention I'd recommend:\n\n1️⃣ **Book an urgent appointment** in the Appointments section\n2️⃣ If it's a severe emergency, please visit your nearest dental clinic or hospital immediately\n\nYour health comes first! 🏥"
        }
    }

    // Help
    if (msg.contains("help") || msg.contains("what can you") || msg.contains("what do you") ||
        msg.contains("مساعدة") || msg.contains("ماذا") || msg.contains("كيف") || msg.contains("ساعدني")) {
        return if (isUserArabic) {
            "أنا مساعد عيادة Smile Scan! 🏥 إليك كيف يمكنني مساعدتك:\n\n📅 **حجز وإدارة المواعيد**\n🦷 **عرض صور الأشعة**\n📋 **التحقق من تقارير التشخيص**\n📁 **مراجعة التاريخ الطبي**\n⚙️ **إعدادات التطبيق**\n\nفقط أخبرني بما تحتاجه، أو اضغط على أحد الأقسام أدناه للانتقال مباشرة! 😊"
        } else {
            "I'm your Smile Scan Clinic assistant! 🏥 Here's how I can help:\n\n📅 **Book/Manage Appointments**\n🦷 **View X-Ray Scans**\n📋 **Check Diagnosis Reports**\n📁 **Review Medical History**\n⚙️ **App Settings**\n\nJust tell me what you need, or tap one of the sections below to go directly! 😊"
        }
    }

    // Dental care tips
    if (msg.contains("tip") || msg.contains("advice") || msg.contains("care") || msg.contains("brush") || msg.contains("floss") ||
        msg.contains("نصيحة") || msg.contains("عناية") || msg.contains("فرشاة") || msg.contains("تنظيف")) {
        return if (isUserArabic) {
            "سؤال رائع عن العناية بالأسنان! 🦷 إليك بعض النصائح من فريق Smile Scan:\n\n• نظف أسنانك مرتين يومياً لمدة دقيقتين في كل مرة\n• استخدم الخيط يومياً لإزالة اللويحات بين الأسنان\n• زر طبيب الأسنان كل 6 أشهر للفحص\n• قلل من الأطعمة والمشروبات السكرية\n• استبدل فرشاة أسنانك كل 3-4 أشهر\n\nهل تود حجز موعد للفحص؟ يمكنني نقلك إلى قسم المواعيد!"
        } else {
            "Great question about dental care! 🦷 Here are some tips from your Smile Scan team:\n\n• Brush your teeth twice a day for 2 minutes each time\n• Floss daily to remove plaque between teeth\n• Visit your dentist every 6 months for checkups\n• Limit sugary foods and drinks\n• Replace your toothbrush every 3-4 months\n\nWould you like to book a checkup appointment? I can take you to the Appointments section!"
        }
    }

    // Default responses based on language
    val isAppAr = AppSettings.currentLanguage == "ar"
    return if (isUserArabic || isAppAr) {
        "شكراً لاستفسارك، $patientName! 😊\n\nلست متأكداً تماماً مما تقصد، لكن يمكنني مساعدتك في:\n📅 حجز موعد\n🦷 عرض صور الأشعة\n📋 قراءة تقارير التشخيص\n📁 مراجعة التاريخ الطبي\n⚙️ الإعدادات\n\nاختر ما يناسبك من الأقسام أدناه!"
    } else {
        "Thank you for your question, $patientName! 😊\n\nI'm not entirely sure what you're looking for, but here's what I can help you with:\n📅 Book or manage appointments\n🦷 View your X-Ray scans and analysis\n📋 Check your diagnosis reports\n📁 Review your medical history\n⚙️ Customize app settings\n\nJust let me know what you need, or tap a section below to go directly!"
    }
}

private fun containsArabic(text: String): Boolean {
    for (char in text) {
        if (Character.UnicodeBlock.of(char) == Character.UnicodeBlock.ARABIC) {
            return true
        }
    }
    return false
}

private fun detectSuggestedSections(text: String): List<SuggestedSection> {
    val lower = text.lowercase()
    return allSections.filter { section ->
        val keyword = section.label.lowercase()
        lower.contains(keyword) ||
        (keyword == "appointments" && (lower.contains("appointment") || lower.contains("موعد") || lower.contains("حجز"))) ||
        (keyword == "xray" && (lower.contains("x-ray") || lower.contains("xray") || lower.contains("x ray") || lower.contains("scan") || lower.contains("أشعة") || lower.contains("اشعة") || lower.contains("تصوير"))) ||
        (keyword == "diagnosis" && (lower.contains("diagnosis") || lower.contains("diagnose") || lower.contains("report") || lower.contains("treatment") || lower.contains("تشخيص") || lower.contains("نتيجة") || lower.contains("علاج"))) ||
        (keyword == "history" && (lower.contains("medical history") || lower.contains("record") || lower.contains("تاريخ") || lower.contains("سجل"))) ||
        (keyword == "settings" && (lower.contains("setting") || lower.contains("إعدادات") || lower.contains("اعدادات")))
    }
}

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onNavigateToAppointments: () -> Unit = {},
    onNavigateToXRay: () -> Unit = {},
    onNavigateToDiagnosis: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    var messageText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val isAr = AppSettings.currentLanguage == "ar"
    val messages = AppSettings.chatHistory
    val listState = rememberLazyListState()
    var isWaiting by remember { mutableStateOf(false) }
    var hasGreeted by remember { mutableStateOf(false) }

    val greetings = if (isAr) listOf(
        "مرحباً بك في عيادة Smile Scan! 🏥",
        "أنا مساعدك الطبي الذكي، أسعد بمساعدتك في أي وقت.",
        "يمكنك سؤالي عن حجز موعد، الأشعة، التشخيصات، التاريخ الطبي، والإعدادات."
    ) else listOf(
        "Welcome to Smile Scan Clinic! 🏥",
        "I'm your smart dental care assistant, here to help you with everything.",
        "Ask me about appointments, X-Rays, diagnosis, medical history, and settings."
    )

    LaunchedEffect(Unit) {
        if (messages.isEmpty() && !hasGreeted) {
            hasGreeted = true
            val initialMsg = greetings.joinToString("\n\n")
            messages.add(ChatMessage(text = initialMsg, isFromMe = false, time = getCurrentTime()))
            AppSettings.saveChatHistory()
        }
        delay(100)
        if (messages.isNotEmpty()) {
            try {
                listState.animateScrollToItem(messages.size - 1)
            } catch (_: Exception) {}
        }
    }

    fun navigateToSection(route: String) {
        when (route) {
            "appointments" -> onNavigateToAppointments()
            "xray" -> onNavigateToXRay()
            "diagnosis" -> onNavigateToDiagnosis()
            "history" -> onNavigateToHistory()
            "settings" -> onNavigateToSettings()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DentalBackground),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(DentalTealDark, DentalTeal, DentalCyan),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, 0f)
                    )
                )
                .padding(top = 8.dp, bottom = 16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🏥", fontSize = 20.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            if (isAr) "عيادة Smile Scan" else "Smile Scan Clinic",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            if (isWaiting) (if (isAr) "يكتب..." else "Typing...") else (if (isAr) "متصل" else "Online"),
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        val flatItems by remember { derivedStateOf {
            val list = mutableListOf<Pair<Int, Boolean>>()
            messages.forEachIndexed { i, msg ->
                list.add(i to false)
                if (!msg.isFromMe && detectSuggestedSections(msg.text).isNotEmpty()) {
                    list.add(i to true)
                }
            }
            list
        } }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(flatItems, key = { index, pair -> if (pair.second) "nav_${pair.first}" else "msg_${pair.first}" }) { _, (msgIdx, isNav) ->
                if (isNav) {
                    val suggested = detectSuggestedSections(messages[msgIdx].text)
                    Row(
                        modifier = Modifier.padding(start = 40.dp, top = 4.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        suggested.forEach { section ->
                            SuggestionChip(
                                onClick = { navigateToSection(section.route) },
                                label = { Text("${section.emoji} ${if (isAr) section.arLabel else section.label}", fontSize = 12.sp) },
                                shape = RoundedCornerShape(20.dp),
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = DentalTeal.copy(alpha = 0.1f),
                                    labelColor = DentalTeal
                                ),
                                border = null
                            )
                        }
                    }
                } else {
                    ChatBubble(messages[msgIdx])
                }
            }
            if (isWaiting) {
                item(key = "typing") {
                    TypingIndicator()
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 4.dp,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it.take(500) },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            if (isAr) "اكتب رسالة..." else stringResource(R.string.type_a_message),
                            color = DentalMuted
                        )
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DentalTeal,
                        unfocusedBorderColor = DentalLine,
                        cursorColor = DentalTeal
                    ),
                    enabled = !isWaiting,
                    singleLine = true
                )
                Spacer(Modifier.width(8.dp))
                FilledIconButton(
                    onClick = {
                        if (messageText.isNotBlank() && !isWaiting) {
                            messageText = messageText.trim()
                            val userMsg = messageText
                            messageText = ""
                            messages.add(ChatMessage(text = userMsg, isFromMe = true, time = getCurrentTime()))
                            AppSettings.saveChatHistory()
                            isWaiting = true

                            scope.launch {
                                try {
                                    delay(800)
                                    val reply = generateSmartResponse(userMsg)
                                    messages.add(ChatMessage(
                                        text = reply,
                                        isFromMe = false,
                                        time = getCurrentTime()
                                    ))
                                    AppSettings.saveChatHistory()
                                } catch (_: Exception) {
                                    messages.add(ChatMessage(
                                        text = if (isAr) "عذراً، حدث خطأ. يرجى المحاولة مرة أخرى." else "Sorry, something went wrong. Please try again.",
                                        isFromMe = false,
                                        time = getCurrentTime()
                                    ))
                                    AppSettings.saveChatHistory()
                                } finally {
                                    isWaiting = false
                                    delay(50)
                                    try {
                                        listState.animateScrollToItem(messages.size - 1)
                                    } catch (_: Exception) {}
                                }
                            }
                        }
                    },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = DentalTeal,
                        disabledContainerColor = DentalMuted
                    ),
                    enabled = !isWaiting && messageText.isNotBlank()
                ) {
                    if (isWaiting) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Filled.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val alignment = if (message.isFromMe) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        contentAlignment = alignment
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (message.isFromMe) Arrangement.End else Arrangement.Start
        ) {
            if (!message.isFromMe) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(DentalTeal, DentalCyan)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏥", fontSize = 16.sp)
                }
                Spacer(Modifier.width(8.dp))
            }

            Surface(
                color = if (message.isFromMe) DentalTeal else Color.White,
                shape = if (message.isFromMe)
                    RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
                else
                    RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp),
                shadowElevation = if (message.isFromMe) 0.dp else 2.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                    Text(
                        text = message.text,
                        color = if (message.isFromMe) Color.White else DentalText,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                    if (message.time.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            message.time,
                            color = if (message.isFromMe) Color.White.copy(alpha = 0.6f) else DentalMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            if (message.isFromMe) {
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(DentalBlueSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = null,
                        tint = DentalTeal,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition()
    val dots = listOf(
        infiniteTransition.animateFloat(0.3f, 1f, infiniteRepeatable(tween(600), RepeatMode.Reverse)),
        infiniteTransition.animateFloat(0.3f, 1f, infiniteRepeatable(tween(600, 200), RepeatMode.Reverse)),
        infiniteTransition.animateFloat(0.3f, 1f, infiniteRepeatable(tween(600, 400), RepeatMode.Reverse))
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(colors = listOf(DentalTeal, DentalCyan))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("🏥", fontSize = 16.sp)
            }
            Spacer(Modifier.width(8.dp))
            Surface(
                shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    dots.forEach { dot ->
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .alpha(dot.value)
                                .clip(CircleShape)
                                .background(DentalTeal.copy(alpha = 0.5f))
                        )
                    }
                }
            }
        }
    }
}

private fun getCurrentTime(): String {
    return try {
        SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date())
    } catch (_: Exception) { "" }
}
