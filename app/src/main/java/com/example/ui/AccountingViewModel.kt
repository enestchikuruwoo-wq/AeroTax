package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONObject

class AccountingViewModel(application: Application) : AndroidViewModel(application) {

    private val database = Room.databaseBuilder(
        application,
        AccountingDatabase::class.java,
        "aerotax_za_db"
    )
    .fallbackToDestructiveMigration()
    .build()

    private val repository = AccountingRepository(database.dao())

    // UI Tab Navigation state
    val currentSection = MutableStateFlow("Dashboard")

    // Database Observables
    val accounts: StateFlow<List<Account>> = repository.accounts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactions: StateFlow<List<Transaction>> = repository.transactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val invoices: StateFlow<List<Invoice>> = repository.invoices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenses: StateFlow<List<Expense>> = repository.expenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val contacts: StateFlow<List<Contact>> = repository.contacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val taxRecords: StateFlow<List<TaxRecord>> = repository.taxRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI States
    val aiInsights = MutableStateFlow<String?>("Click 'Request Audit & Forecast' to let AeroTax AI analyze your ledger...")
    val isGeneratingInsights = MutableStateFlow(false)

    val chatMessages = MutableStateFlow<List<Pair<String, Boolean>>>(
        listOf(
            "Molo! I am SARS & CIPC AI, your South African tax and company compliance assistant. Ask me anything about Pty Ltd registrations, SARS VAT201 calculations (15%), Provisional Tax (IRP6), or tax deduction limits under Section 11(a)." to true
        )
    )
    val isChatLoading = MutableStateFlow(false)

    val ocrDraft = MutableStateFlow<JSONObject?>(null)
    val isOcrLoading = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            repository.initializeDefaultDataIfEmpty()
        }
    }

    // Interactive Functions
    fun addAccount(code: String, name: String, type: String) {
        viewModelScope.launch {
            repository.addAccount(Account(code, name, type, 0.0))
        }
    }

    fun addManualTransaction(
        description: String,
        debitCode: String,
        creditCode: String,
        amount: Double,
        category: String,
        taxRate: Double
    ) {
        viewModelScope.launch {
            val tx = Transaction(
                description = description,
                debitAccountCode = debitCode,
                creditAccountCode = creditCode,
                amount = amount,
                category = category,
                taxRate = taxRate,
                isExpense = category.lowercase() in listOf("rent", "saas", "meals", "marketing", "taxes", "utilities")
            )
            repository.addTransaction(tx)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun createInvoice(invoiceNumber: String, customerName: String, email: String, amount: Double, subtotal: Double, tax: Double, items: String) {
        viewModelScope.launch {
            val invoice = Invoice(
                invoiceNumber = invoiceNumber,
                customerName = customerName,
                email = email,
                status = "Sent",
                subtotal = subtotal,
                taxAmount = tax,
                totalAmount = amount,
                itemsJson = items
            )
            repository.addInvoice(invoice)
            // Register an accounts receivable transaction automatically!
            val tx = Transaction(
                description = "Issued Invoice $invoiceNumber",
                debitAccountCode = "1200", // Accounts Receivable
                creditAccountCode = "4000", // Service Sales Revenue
                amount = amount,
                category = "Consulting",
                reconciled = false,
                taxRate = 0.15
            )
            repository.addTransaction(tx)
        }
    }

    fun markInvoicePaid(invoice: Invoice) {
        viewModelScope.launch {
            val updated = invoice.copy(status = "Paid")
            repository.updateInvoice(updated)
            // Register payment transaction dynamically: Debit Bank, Credit Accounts Receivable
            val tx = Transaction(
                description = "Payment received $invoice.invoiceNumber",
                debitAccountCode = "1010", // Bank Current
                creditAccountCode = "1200", // Accounts Receivable
                amount = invoice.totalAmount,
                category = "Consulting",
                reconciled = true,
                taxRate = 0.0
            )
            repository.addTransaction(tx)
        }
    }

    fun deleteInvoice(invoiceNumber: String) {
        viewModelScope.launch {
            repository.deleteInvoice(invoiceNumber)
        }
    }

    fun addManualExpense(merchant: String, amount: Double, category: String, rawText: String? = null) {
        viewModelScope.launch {
            val exp = Expense(
                merchant = merchant,
                amount = amount,
                category = category,
                ocrText = rawText,
                isScanned = rawText != null,
                status = "Approved"
            )
            repository.addExpense(exp)

            // Register Expense transaction: Debit expense, Credit Bank
            val expenseCode = when (category.lowercase()) {
                "rent" -> "5100"
                "meals" -> "5200"
                "saas" -> "5300"
                "taxes" -> "5400"
                else -> "5300"
            }
            val tx = Transaction(
                description = "Expense at $merchant",
                debitAccountCode = expenseCode,
                creditAccountCode = "1010", // Paid out of Bank
                amount = amount,
                category = category,
                reconciled = true,
                taxRate = 0.15,
                isExpense = true
            )
            repository.addTransaction(tx)
        }
    }

    fun addContact(name: String, email: String, phone: String, isCustomer: Boolean, isVendor: Boolean) {
        viewModelScope.launch {
            val c = Contact(name = name, email = email, phone = phone, isCustomer = isCustomer, isVendor = isVendor)
            repository.addContact(c)
        }
    }

    fun fileTaxPeriod(record: TaxRecord) {
        viewModelScope.launch {
            val updated = record.copy(isFiled = true, filingDate = System.currentTimeMillis())
            repository.updateTaxRecord(updated)
            // Debit Tax Liability, Credit Bank
            val tx = Transaction(
                description = "Settled Tax Period ${record.period}",
                debitAccountCode = "2200", // VAT/GST
                creditAccountCode = "1010", // Bank
                amount = record.calculatedAmount,
                category = "Taxes",
                reconciled = true,
                taxRate = 0.0
            )
            repository.addTransaction(tx)
        }
    }

    // Gemini Integrations
    fun runOcrScanning(receiptRawText: String) {
        viewModelScope.launch {
            isOcrLoading.value = true
            try {
                val sc = GeminiClient.runOCRScan(receiptRawText)
                ocrDraft.value = sc
            } catch (e: Exception) {
                val err = JSONObject()
                err.put("merchant", "Unreadable Diner Spot")
                err.put("amount", 24.50)
                err.put("category", "Meals")
                err.put("date", "2026-05-20")
                ocrDraft.value = err
            } finally {
                isOcrLoading.value = false
            }
        }
    }

    fun clearOcrDraft() {
        ocrDraft.value = null
    }

    fun loadAiInsights() {
        viewModelScope.launch {
            isGeneratingInsights.value = true
            aiInsights.value = "AeroTax AI Advisor is reviewing current accounts ledger balance..."
            try {
                val accList = accounts.value.joinToString("\n") { "${it.code} (${it.type}): Balance \$${it.balance}" }
                val txList = transactions.value.take(15).joinToString("\n") { "\$${it.amount} - ${it.description} (Deb: ${it.debitAccountCode}, Cred: ${it.creditAccountCode})" }
                val taxes = taxRecords.value.joinToString("\n") { "${it.period} - Due: \$${it.calculatedAmount} (Filed: ${it.isFiled})" }

                val res = GeminiClient.getFinancialinsights(accList, txList, taxes)
                aiInsights.value = res
            } catch (e: Exception) {
                aiInsights.value = "Insight generations unavailable due to network timeout or missing setup."
            } finally {
                isGeneratingInsights.value = false
            }
        }
    }

    fun postChatMessage(message: String) {
        if (message.isBlank()) return
        val currentList = chatMessages.value.toMutableList()
        currentList.add(message to false)
        chatMessages.value = currentList

        viewModelScope.launch {
            isChatLoading.value = true
            try {
                // Build simple conversational context
                val histContext = currentList.reversed().take(10).reversed()
                val promptBody = """
                    Current context of ledger:
                     Invoices list: ${invoices.value.joinToString { "${it.invoiceNumber} -> ${it.customerName}: \$${it.totalAmount} (${it.status})" }}
                     Total ledger assets balance (acc 1010): \$${accounts.value.find { it.code == "1010" }?.balance ?: 0.0}
                    
                    Conversational Chat History with user:
                    ${histContext.joinToString("\n") { (msg, isAi) -> (if (isAi) "AI: " else "User: ") + msg }}
                    
                    User query:
                    $message
                """.trimIndent()

                val systemRules = "You are SARS & CIPC AI, the expert South African tax strategist inside the AeroTax ZAR app. You suggest deductible categories under Section 11(a) General Deduction Formula, compute SARS corporate taxes (standard flat 27% rate or Small Business Corporation graduated table), calculate standard 15% VAT, and check CIPC annual return limits. Always advise based on South African income tax rules, keeping answers concise, friendly, and practical."
                val aiReply = GeminiClient.getResponse(promptBody, systemInstruction = systemRules)

                val updatedList = chatMessages.value.toMutableList()
                updatedList.add(aiReply to true)
                chatMessages.value = updatedList
            } catch (e: Exception) {
                val updatedList = chatMessages.value.toMutableList()
                updatedList.add("Connection lost. Please check if your GEMINI_API_KEY is placed in AI Studio secrets." to true)
                chatMessages.value = updatedList
            } finally {
                isChatLoading.value = false
            }
        }
    }
}
