package com.example.dentalclinic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dentalclinic.R
import com.example.dentalclinic.data.AppSettings
import com.example.dentalclinic.data.fake.FakeDentalData
import com.example.dentalclinic.ui.components.DentalHeader
import com.example.dentalclinic.ui.theme.DentalTeal
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ChatMessage(val text: String, val isFromMe: Boolean)

@Composable
fun ChatScreen(modifier: Modifier = Modifier, onBack: () -> Unit) {
    var messageText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val isAr = AppSettings.currentLanguage == "ar"
    
    // Load existing messages from FakeDentalData and add AI greeting
    val messages = remember { 
        val list = mutableStateListOf<ChatMessage>()
        
        // Add historical clinic messages
        FakeDentalData.messages.forEach { msg ->
            list.add(ChatMessage(text = msg.text, isFromMe = msg.fromPatient))
        }
        
        // Add AI Greeting if not already there
        val initialMsg = if (isAr) 
            "مرحباً! أنا مساعدك الذكي في عيادة الأسنان. كيف يمكنني مساعدتك اليوم؟"
        else 
            "Hello! I am your AI Dental Assistant. How can I help you today?"
        
        list.add(ChatMessage(text = initialMsg, isFromMe = false))
        list
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        DentalHeader(
            title = if (isAr) "محادثة المساعد" else stringResource(R.string.doctor_chat),
            subtitle = if (isAr) "الذكاء الاصطناعي" else "AI Assistant",
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { message ->
                ChatBubble(message)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text(if (isAr) "اكتب رسالة..." else stringResource(R.string.type_a_message)) },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DentalTeal,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (messageText.isNotBlank()) {
                        val userMsg = messageText
                        messages.add(ChatMessage(text = userMsg, isFromMe = true))
                        messageText = ""
                        
                        // AI Response Logic
                        scope.launch {
                            delay(1000) // Simulating thinking
                            val response = getAIResponse(userMsg)
                            messages.add(ChatMessage(text = response, isFromMe = false))
                        }
                    }
                },
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = DentalTeal)
            ) {
                Text(">", color = Color.White)
            }
        }
    }
}

private fun getAIResponse(input: String): String {
    val lowerInput = input.lowercase()
    val isAr = AppSettings.currentLanguage == "ar"
    
    // Knowledge Base Access
    val doctors = FakeDentalData.dentists
    val xrays = FakeDentalData.xRays

    return when {
        // Greetings
        lowerInput.contains("hello") || lowerInput.contains("hi") || lowerInput.contains("مرحبا") || lowerInput.contains("اهلا") -> {
            if (isAr) "مرحباً! أنا هنا للمساعدة في استفساراتك حول عيادة الأسنان. ماذا يدور في ذهنك؟"
            else "Hello! I'm here to assist with your dental inquiries. What's on your mind?"
        }

        // Doctors
        lowerInput.contains("doctor") || lowerInput.contains("dentist") || lowerInput.contains("طبيب") || lowerInput.contains("دكتور") -> {
            val names = doctors.joinToString(", ") { it.name }
            if (isAr) "لدينا نخبة من الأطباء: $names. يمكنك معرفة المزيد عن تخصصاتهم في قسم الأطباء."
            else "We have excellent doctors: $names. You can learn more about their specialties in the Doctors section."
        }

        // Appointments
        lowerInput.contains("appointment") || lowerInput.contains("booking") || lowerInput.contains("موعد") || lowerInput.contains("حجز") -> {
            if (isAr) "بالتأكيد! يمكنني إخبارك عن خدماتنا أو مساعدتك في فهم أي أعراض تشعر بها قبل الحجز. هل تود معرفة المزيد عن (التنظيف، التبييض، أو علاج الآلام)؟ عندما تكون مستعداً، يمكنك اختيار الموعد المناسب لك في قسم 'المواعيد'."
            else "Certainly! I can tell you about our services or help you understand any symptoms before you book. Would you like to know more about (Cleaning, Whitening, or Pain Relief)? When you're ready, you can choose the perfect time for yourself in the 'Appointments' section."
        }

        // X-Rays
        lowerInput.contains("xray") || lowerInput.contains("x-ray") || lowerInput.contains("أشعة") || lowerInput.contains("اشعه") -> {
            val latestXray = xrays.firstOrNull()
            if (latestXray != null) {
                if (isAr) "أحدث أشعة لك كانت ${latestXray.title} بتاريخ ${latestXray.date}. النتيجة: ${latestXray.finding}"
                else "Your latest X-Ray was ${latestXray.title} on ${latestXray.date}. Finding: ${latestXray.finding}"
            } else {
                if (isAr) "لا توجد سجلات أشعة في ملفك حالياً."
                else "No X-ray records found in your profile at the moment."
            }
        }

        // Pain / Emergency
        lowerInput.contains("pain") || lowerInput.contains("hurt") || lowerInput.contains("emergency") || lowerInput.contains("ألم") || lowerInput.contains("طوارئ") -> {
            if (isAr) "أنا آسف لسماع أنك تشعر بالألم. ننصحك بالمضمضة بماء دافئ وملح وحجز موعد طارئ فوراً. هل تريد رقم الطوارئ؟"
            else "I'm sorry to hear you're in pain. We recommend rinsing with warm salt water and booking an emergency appointment immediately. Would you like the emergency number?"
        }

        // Cost
        lowerInput.contains("price") || lowerInput.contains("cost") || lowerInput.contains("سعر") || lowerInput.contains("تكلفة") -> {
            if (isAr) "تختلف الأسعار حسب العلاج. يمكنك رؤية قائمة الأسعار العامة في قسم الخدمات أو سؤال العيادة مباشرة."
            else "Prices vary depending on the treatment. You can see our general price list in the 'Services' section or ask the clinic directly."
        }

        // Insurance
        lowerInput.contains("insurance") || lowerInput.contains("تأمين") || lowerInput.contains("تامين") -> {
            val patient = FakeDentalData.patient
            if (isAr) "تأمينك المسجل لدينا هو: ${patient.insurance}. يرجى التحقق من تغطية العلاج المحدد مع شركة التأمين."
            else "Your insurance on file is: ${patient.insurance}. Please check specific treatment coverage with your provider."
        }

        // Profile / Me
        lowerInput.contains("who am i") || lowerInput.contains("my name") || lowerInput.contains("من أنا") || lowerInput.contains("اسمي") -> {
            val patient = FakeDentalData.patient
            if (isAr) "أنت ${patient.name}، وعمرك ${patient.age} عاماً. هل تود رؤية بقية تفاصيل ملفك الشخصي؟"
            else "You are ${patient.name}, ${patient.age} years old. Would you like to see the rest of your profile details?"
        }

        else -> {
            if (isAr) "شكراً لرسالتك. أنا مساعد افتراضي وأتعلم باستمرار. للاستشارة الطبية الدقيقة، يرجى التحدث مع الطبيب المختص خلال زيارتك القادمة."
            else "Thank you for your message. I'm an AI assistant and I'm constantly learning. For precise medical advice, please speak with your specialist during your next visit."
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val alignment = if (message.isFromMe) Alignment.CenterEnd else Alignment.CenterStart
    val color = if (message.isFromMe) DentalTeal else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (message.isFromMe) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
    val shape = if (message.isFromMe) {
        RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
    }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
        Surface(
            color = color,
            shape = shape,
            tonalElevation = 2.dp
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                color = textColor,
                fontSize = 15.sp
            )
        }
    }
}
