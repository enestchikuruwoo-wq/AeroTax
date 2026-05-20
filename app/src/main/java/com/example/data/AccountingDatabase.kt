package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey val code: String, // e.g., "1010", "1200", "2000"
    val name: String,
    val type: String, // Asset, Liability, Equity, Revenue, Expense
    val balance: Double = 0.0
)

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Long = System.currentTimeMillis(),
    val description: String,
    val debitAccountCode: String,
    val creditAccountCode: String,
    val amount: Double,
    val category: String = "General",
    val reconciled: Boolean = false,
    val taxRate: Double = 0.15, // Standard 15% rate
    val isExpense: Boolean = false
)

@Entity(tableName = "invoices")
data class Invoice(
    @PrimaryKey val invoiceNumber: String,
    val customerName: String,
    val email: String = "",
    val date: Long = System.currentTimeMillis(),
    val dueDate: Long = System.currentTimeMillis() + 14 * 24 * 60 * 60 * 1000L, // 14 days later
    val status: String, // Draft, Sent, Paid, Overdue
    val subtotal: Double,
    val taxAmount: Double,
    val discount: Double = 0.0,
    val totalAmount: Double,
    val itemsJson: String // Format: "Item Name, qty, price; Item Name 2, qty, price"
)

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val merchant: String,
    val date: Long = System.currentTimeMillis(),
    val amount: Double,
    val category: String,
    val ocrText: String? = null,
    val isScanned: Boolean = false,
    val status: String = "Draft", // Draft, Approved, Paid
    val mileageDistance: Double = 0.0
)

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val phone: String = "",
    val isCustomer: Boolean = true,
    val isVendor: Boolean = false,
    val balance: Double = 0.0
)

@Entity(tableName = "tax_records")
data class TaxRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val period: String, // e.g., "VAT Q2 2026", "Corporate 2026"
    val taxType: String, // VAT, GST, Corporate Tax, PAYE
    val baseAmount: Double,
    val calculatedAmount: Double,
    val deadline: Long,
    val isFiled: Boolean = false,
    val filingDate: Long? = null
)

@Dao
interface AccountingDao {
    // Accounts Chart
    @Query("SELECT * FROM accounts ORDER BY code ASC")
    fun getAllAccounts(): Flow<List<Account>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: Account)

    @Update
    suspend fun updateAccount(account: Account)

    // Transactions General Ledger
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    // Invoices
    @Query("SELECT * FROM invoices ORDER BY date DESC")
    fun getAllInvoices(): Flow<List<Invoice>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: Invoice)

    @Update
    suspend fun updateInvoice(invoice: Invoice)

    @Query("DELETE FROM invoices WHERE invoiceNumber = :invoiceNumber")
    suspend fun deleteInvoiceByNumber(invoiceNumber: String)

    // Expenses
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Update
    suspend fun updateExpense(expense: Expense)

    // Contacts
    @Query("SELECT * FROM contacts ORDER BY name ASC")
    fun getAllContacts(): Flow<List<Contact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact)

    // Tax Records
    @Query("SELECT * FROM tax_records ORDER BY deadline ASC")
    fun getAllTaxRecords(): Flow<List<TaxRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaxRecord(taxRecord: TaxRecord)

    @Update
    suspend fun updateTaxRecord(taxRecord: TaxRecord)
}

@Database(
    entities = [
        Account::class,
        Transaction::class,
        Invoice::class,
        Expense::class,
        Contact::class,
        TaxRecord::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AccountingDatabase : RoomDatabase() {
    abstract fun dao(): AccountingDao
}
