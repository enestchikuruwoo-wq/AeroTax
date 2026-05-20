package com.example.data

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiClient {
    private const val MODEL = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val mediaType = "application/json".toMediaType()

    suspend fun getResponse(prompt: String, systemInstruction: String? = null): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "API Key Required: Please add your GEMINI_API_KEY to the AI Studio Secrets panel."
        }

        val requestBodyJson = JSONObject()
        val contentsArray = JSONArray()
        val contentObj = JSONObject()
        val partsArray = JSONArray()
        val partObj = JSONObject()
        partObj.put("text", prompt)
        partsArray.put(partObj)
        contentObj.put("parts", partsArray)
        contentsArray.put(contentObj)
        requestBodyJson.put("contents", contentsArray)

        if (systemInstruction != null) {
            val systemInstructionObj = JSONObject()
            val systemPartsArray = JSONArray()
            val systemPartObj = JSONObject()
            systemPartObj.put("text", systemInstruction)
            systemPartsArray.put(systemPartObj)
            systemInstructionObj.put("parts", systemPartsArray)
            requestBodyJson.put("systemInstruction", systemInstructionObj)
        }

        val body = requestBodyJson.toString().toRequestBody(mediaType)
        val request = Request.Builder()
            .url("$BASE_URL?key=$apiKey")
            .post(body)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errString = response.body?.string() ?: ""
                    return@withContext "API Call Failed with Code ${response.code}: $errString"
                }
                val responseBodyStr = response.body?.string() ?: return@withContext "No content returned from Gemini."
                val responseJson = JSONObject(responseBodyStr)
                val candidatesArray = responseJson.optJSONArray("candidates") ?: return@withContext "No candidates found"
                val firstCandidate = candidatesArray.optJSONObject(0) ?: return@withContext "No candidate returned"
                val content = firstCandidate.optJSONObject("content") ?: return@withContext "No content returned"
                val parts = content.optJSONArray("parts") ?: return@withContext "No parts returned"
                val firstPart = parts.optJSONObject(0) ?: return@withContext "No part returned"
                return@withContext firstPart.optString("text", "No text body returned")
            }
        } catch (e: Exception) {
            "Request failed: ${e.localizedMessage ?: "Unknown network exception"}"
        }
    }

    suspend fun runOCRScan(rawReceiptText: String): JSONObject = withContext(Dispatchers.IO) {
        val prompt = """
            You are an expert AI Receipt OCR scanner. Analyze the raw receipt text printed below and extract the following details in a clean JSON format.
            Do not markdown or add code fences, output ONLY raw JSON that matches this schema:
            {
               "merchant": "Extracted Merchant Name",
               "amount": 0.00,
               "category": "Extracted Category (one of: Rent, SaaS, Meals, Taxes, Consulting, Marketing, General)",
               "date": "YYYY-MM-DD"
            }
            
            Raw Receipt Text:
            $rawReceiptText
        """.trimIndent()

        val response = getResponse(prompt, systemInstruction = "You are a specialized receipt OCR parser. Always return valid, compact raw JSON conforming exactly to the requested schema. No conversational chatter.")
        try {
            // Trim markdown backticks just in case
            val cleanJson = response.trim()
                .removePrefix("```json")
                .removeSuffix("```")
                .trim()
            JSONObject(cleanJson)
        } catch (e: Exception) {
            // Fallback
            val obj = JSONObject()
            obj.put("merchant", "Extracted Merchant")
            obj.put("amount", 0.0)
            obj.put("category", "General")
            obj.put("date", "2026-05-20")
            obj
        }
    }

    suspend fun getFinancialinsights(
        accountsStr: String,
        transactionsStr: String,
        taxRecordsStr: String
    ): String {
        val systemMessage = """
            You are the SARS & CIPC Lead Advisor, a premier South African CA(SA) chartered accountant and corporate tax consultant.
            Analyze the business's Chart of Accounts, Ledger Transactions, and Pending SARS Tax / CIPC Obligations.
            Generate:
            1. **SARS Tax Guidance**: Estimate taxable income, VAT liabilities (15% standard rate), and advise on Provisional Tax (IRP6) obligations.
            2. **CIPC Annual Filing Review**: Advise on required annual return timelines and state fee brackets (e.g. R450 base for Pty Ltd turnover below R1m, or higher tiers) to keep the company active.
            3. **SA Tax Deductions**: Highlight local optimization parameters under Section 11(a) of the Income Tax Act (Internet/MTN Fibre, Sandton Rent, travel claim logs).
            4. **Working Capital Strategy**: Local financial health indicators for South African businesses.
            Keep formatting highly readable with bold headers, concise bullet points, and express all currency in South African Rand (R).
        """.trimIndent()

        val prompt = """
            Chart of Accounts:
            $accountsStr
            
            Ledger Transactions:
            $transactionsStr
            
            Pending Tax Records:
            $taxRecordsStr
        """.trimIndent()

        return getResponse(prompt, systemInstruction = systemMessage)
    }
}
