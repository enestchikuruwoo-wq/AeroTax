package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AccountingRepository(private val dao: AccountingDao) {

    val accounts: Flow<List<Account>> = dao.getAllAccounts()
    val transactions: Flow<List<Transaction>> = dao.getAllTransactions()
    val invoices: Flow<List<Invoice>> = dao.getAllInvoices()
    val expenses: Flow<List<Expense>> = dao.getAllExpenses()
    val contacts: Flow<List<Contact>> = dao.getAllContacts()
    val taxRecords: Flow<List<TaxRecord>> = dao.getAllTaxRecords()

    // Business functions
    suspend fun addAccount(account: Account) = withContext(Dispatchers.IO) {
        dao.insertAccount(account)
    }

    suspend fun updateAccount(account: Account) = withContext(Dispatchers.IO) {
        dao.updateAccount(account)
    }

    suspend fun addTransaction(transaction: Transaction) = withContext(Dispatchers.IO) {
        dao.insertTransaction(transaction)
        // Adjust Account balances according to double-entry rules
        adjustBalancesForTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) = withContext(Dispatchers.IO) {
        dao.deleteTransaction(transaction)
    }

    suspend fun addInvoice(invoice: Invoice) = withContext(Dispatchers.IO) {
        dao.insertInvoice(invoice)
    }

    suspend fun deleteInvoice(invoiceNumber: String) = withContext(Dispatchers.IO) {
        dao.deleteInvoiceByNumber(invoiceNumber)
    }

    suspend fun updateInvoice(invoice: Invoice) = withContext(Dispatchers.IO) {
        dao.updateInvoice(invoice)
    }

    suspend fun addExpense(expense: Expense) = withContext(Dispatchers.IO) {
        dao.insertExpense(expense)
    }

    suspend fun updateExpense(expense: Expense) = withContext(Dispatchers.IO) {
        dao.updateExpense(expense)
    }

    suspend fun addContact(contact: Contact) = withContext(Dispatchers.IO) {
        dao.insertContact(contact)
    }

    suspend fun addTaxRecord(taxRecord: TaxRecord) = withContext(Dispatchers.IO) {
        dao.insertTaxRecord(taxRecord)
    }

    suspend fun updateTaxRecord(taxRecord: TaxRecord) = withContext(Dispatchers.IO) {
        dao.updateTaxRecord(taxRecord)
    }

    private suspend fun adjustBalancesForTransaction(tx: Transaction) {
        // Simple balance sheet calculation updates
        val debitAcc = dao.getAllAccounts().first().find { it.code == tx.debitAccountCode }
        val creditAcc = dao.getAllAccounts().first().find { it.code == tx.creditAccountCode }

        if (debitAcc != null) {
            val mult = if (debitAcc.type == "Asset" || debitAcc.type == "Expense") 1.0 else -1.0
            dao.updateAccount(debitAcc.copy(balance = debitAcc.balance + (tx.amount * mult)))
        }

        if (creditAcc != null) {
            val mult = if (creditAcc.type == "Liability" || creditAcc.type == "Equity" || creditAcc.type == "Revenue") 1.0 else -1.0
            dao.updateAccount(creditAcc.copy(balance = creditAcc.balance + (tx.amount * mult)))
        }
    }

    suspend fun initializeDefaultDataIfEmpty() = withContext(Dispatchers.IO) {
        val currentAccounts = dao.getAllAccounts().first()
        if (currentAccounts.isNotEmpty()) return@withContext // already populated

        // 1. Chart Of Accounts
        val defaultAccounts = listOf(
            Account("1010", "ZAR Bank Current Account", "Asset", 164000.00),
            Account("1200", "Accounts Receivable", "Asset", 47000.00),
            Account("1400", "Inventory Asset", "Asset", 25000.00),
            Account("2000", "Accounts Payable", "Liability", 15000.00),
            Account("2200", "SARS VAT liability", "Liability", 12750.00),
            Account("3000", "Share Capital (Equity)", "Equity", 150000.00),
            Account("4000", "Consulting & Service Sales", "Revenue", 100000.00),
            Account("5100", "Sandton Office Rent", "Expense", 15000.00),
            Account("5200", "SA Marketing & Promo", "Expense", 6000.00),
            Account("5300", "MTN Fibre & Cloud SaaS", "Expense", 4750.00),
            Account("5400", "CIPC & SARS Fees Expense", "Expense", 5000.00)
        )
        for (acc in defaultAccounts) {
            dao.insertAccount(acc)
        }

        // 2. Contacts
        val defaultContacts = listOf(
            Contact(0, "Vodacom Pty Ltd", "billing@vodacom.co.za", "+27-11-5461111", isCustomer = true, isVendor = false, balance = 50000.0),
            Contact(0, "Eskom Holdings SOC Ltd", "tariffs@eskom.co.za", "+27-86-0037566", isCustomer = true, isVendor = false, balance = 47000.0),
            Contact(0, "Shoprite Holdings Ltd", "accounts@shoprite.co.za", "+27-21-9804000", isCustomer = true, isVendor = false, balance = 0.0),
            Contact(0, "Growthpoint Properties", "sandton@growthpoint.co.za", "+27-11-9446000", isCustomer = false, isVendor = true, balance = 15000.0),
            Contact(0, "MTN South Africa Ltd", "corporate@mtn.co.za", "+27-83-1234567", isCustomer = false, isVendor = true, balance = 4750.0)
        )
        for (contact in defaultContacts) {
            dao.insertContact(contact)
        }

        // 3. Transactions (Paid)
        val t1 = Transaction(
            0,
            System.currentTimeMillis() - 15 * 24 * 60 * 60 * 1000L,
            "Invoice VODACOM-001 (Paid in Full)",
            "1010",
            "4000",
            50000.0,
            "Consulting",
            reconciled = true,
            taxRate = 0.15
        )
        val t2 = Transaction(
            0,
            System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000L,
            "Rent - Sandton Suite 4B",
            "5100",
            "1010",
            15000.0,
            "Rent",
            reconciled = true,
            taxRate = 0.15
        )
        val t3 = Transaction(
            0,
            System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000L,
            "MTN LTE Business Fibre Billing",
            "5300",
            "1010",
            4750.0,
            "Utilities",
            reconciled = true,
            taxRate = 0.15
        )
        val t4 = Transaction(
            0,
            System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000L,
            "Business Lunch at Mugg & Bean",
            "5200",
            "1010",
            1250.0,
            "Marketing",
            reconciled = false,
            taxRate = 0.15
        )
        dao.insertTransaction(t1)
        dao.insertTransaction(t2)
        dao.insertTransaction(t3)
        dao.insertTransaction(t4)

        // 4. Invoices
        val inv1 = Invoice(
            "INV-2026-001",
            "Vodacom Pty Ltd",
            "billing@vodacom.co.za",
            System.currentTimeMillis() - 15 * 24 * 60 * 60 * 1000L,
            System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000L,
            "Paid",
            43478.26,
            6521.74,
            0.0,
            50000.0,
            "IT Infrastructure Consulting (ZAR), 1, 50000.0"
        )

        val inv2 = Invoice(
            "INV-2026-002",
            "Eskom Holdings SOC Ltd",
            "tariffs@eskom.co.za",
            System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000L,
            System.currentTimeMillis() + 11 * 24 * 60 * 60 * 1000L,
            "Sent",
            40869.57,
            6130.43,
            0.0,
            47000.00,
            "Billing Automation Framework, 1, 47000.00"
        )
        val inv3 = Invoice(
            "INV-2026-003",
            "Shoprite Holdings Ltd",
            "accounts@shoprite.co.za",
            System.currentTimeMillis(),
            System.currentTimeMillis() + 14 * 24 * 60 * 60 * 1000L,
            "Draft",
            10434.78,
            1565.22,
            0.0,
            12000.0,
            "SA Logistics Dashboard, 24, 500.0"
        )
        dao.insertInvoice(inv1)
        dao.insertInvoice(inv2)
        dao.insertInvoice(inv3)

        // 5. Expenses (Reconciliation & OCR receipt samples)
        val exp1 = Expense(
            0,
            "Growthpoint Properties Ltd",
            System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000L,
            15000.00,
            "Rent",
            "TAX INVOICE\nGrowthpoint Properties Ltd\nAmount: R15000.00\nRent for Office Suite 4B\nVAT Number: 492019921\nStatus: PAID",
            isScanned = true,
            status = "Approved"
        )
        val exp2 = Expense(
            0,
            "MTN South Africa Ltd",
            System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000L,
            4750.00,
            "SaaS",
            "TAX INVOICE #88129\nMTN South Africa Ltd\nTotal due: R4750.00\nVAT (15%): R619.57\nStatus: Paid via EFT",
            isScanned = true,
            status = "Approved"
        )
        val exp3 = Expense(
            0,
            "Mugg & Bean Sandton",
            System.currentTimeMillis() - 12 * 60 * 60 * 1000L,
            955.00,
            "Meals",
            "MUGG & BEAN SANDTON CITY\n2026-05-20\nSubtotal: R830.43\nVAT (15%): R124.57\nTOTAL: R955.00\nSARS VAT Vendor #4910248\nThank you!",
            isScanned = false,
            status = "Draft"
        )
        dao.insertExpense(exp1)
        dao.insertExpense(exp2)
        dao.insertExpense(exp3)

        // 6. Tax returns estimation / compliance logs
        val tr1 = TaxRecord(
            0,
            "SARS VAT201 (Period 2026/02)",
            "VAT",
            100000.00,
            15000.00,
            System.currentTimeMillis() + 40 * 24 * 60 * 60 * 1000L, // 40 days
            isFiled = false
        )
        val tr2 = TaxRecord(
            0,
            "SARS IRP6 Provisional Tax 1st Period",
            "Provisional Tax",
            150000.00,
            42000.00,
            System.currentTimeMillis() + 180 * 24 * 60 * 60 * 1000L, // 180 days
            isFiled = false
        )
        val tr3 = TaxRecord(
            0,
            "CIPC Annual Return 2026 (Pty) Ltd",
            "CIPC Return",
            10000.00,
            450.00,
            System.currentTimeMillis() + 20 * 24 * 60 * 60 * 1000L, // 20 days
            isFiled = false
        )
        dao.insertTaxRecord(tr1)
        dao.insertTaxRecord(tr2)
        dao.insertTaxRecord(tr3)
    }
}
