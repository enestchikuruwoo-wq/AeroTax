package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AeroTaxDashboardScreen(viewModel: AccountingViewModel) {
    val currentView by viewModel.currentSection.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val invoices by viewModel.invoices.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val contacts by viewModel.contacts.collectAsState()
    val taxRecords by viewModel.taxRecords.collectAsState()

    var activeCompany by remember { mutableStateOf("Acme Designs Inc.") }
    var showCompanyDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier
                            .clickable { showCompanyDialog = true }
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "CLOUD LEDGER V4.2",
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 1.2.sp,
                            color = GeometricBlue.copy(alpha = 0.7f)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = activeCompany,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextDarkSlate,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Switch Organization",
                                tint = TextDarkSlate.copy(alpha = 0.6f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                actions = {
                    // Geometric Balance Action components: Circle Notification and Circle JD User Avatar
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(38.dp)
                            .clip(RoundedCornerShape(19.dp))
                            .background(Color.White)
                            .border(1.dp, GridDashedLine, RoundedCornerShape(19.dp))
                            .clickable { /* Notifications click */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🔔", fontSize = 16.sp)
                    }
                    
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(38.dp)
                            .clip(RoundedCornerShape(19.dp))
                            .background(GeometricBlue)
                            .clickable { /* User Profile click */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "JD",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BaseLightBg
                )
            )
        },
        bottomBar = {
            Column {
                Divider(color = GridDashedLine)
                // Responsive and Scrollable M3 Tab Bar for seamless section transition (6 modules)
                ScrollableTabRow(
                    selectedTabIndex = when (currentView) {
                        "Dashboard" -> 0
                        "Ledger" -> 1
                        "Invoices" -> 2
                        "Expenses" -> 3
                        "Tax & AI" -> 4
                        "CRM" -> 5
                        else -> 0
                    },
                    containerColor = CardWhite,
                    contentColor = GeometricBlue,
                    edgePadding = 12.dp
                ) {
                    val activeColor = GeometricBlue
                    val idleColor = TextSlateGrey
                    
                    Tab(
                        selected = currentView == "Dashboard",
                        selectedContentColor = activeColor,
                        unselectedContentColor = idleColor,
                        onClick = { viewModel.currentSection.value = "Dashboard" },
                        text = { Text("Dashboard", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        icon = { Icon(Icons.Default.TrendingUp, "Dashboard", modifier = Modifier.size(20.dp)) }
                    )
                    Tab(
                        selected = currentView == "Ledger",
                        selectedContentColor = activeColor,
                        unselectedContentColor = idleColor,
                        onClick = { viewModel.currentSection.value = "Ledger" },
                        text = { Text("Ledger", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        icon = { Icon(Icons.Default.AccountBalance, "General Ledger", modifier = Modifier.size(20.dp)) }
                    )
                    Tab(
                        selected = currentView == "Invoices",
                        selectedContentColor = activeColor,
                        unselectedContentColor = idleColor,
                        onClick = { viewModel.currentSection.value = "Invoices" },
                        text = { Text("Invoices", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        icon = { Icon(Icons.Default.Description, "Invoicing", modifier = Modifier.size(20.dp)) }
                    )
                    Tab(
                        selected = currentView == "Expenses",
                        selectedContentColor = activeColor,
                        unselectedContentColor = idleColor,
                        onClick = { viewModel.currentSection.value = "Expenses" },
                        text = { Text("Expenses", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        icon = { Icon(Icons.Default.Receipt, "Expenses Log", modifier = Modifier.size(20.dp)) }
                    )
                    Tab(
                        selected = currentView == "Tax & AI",
                        selectedContentColor = activeColor,
                        unselectedContentColor = idleColor,
                        onClick = { viewModel.currentSection.value = "Tax & AI" },
                        text = { Text("Tax & AI", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        icon = { Icon(Icons.Default.Psychology, "Tax AI Tool", modifier = Modifier.size(20.dp)) }
                    )
                    Tab(
                        selected = currentView == "CRM",
                        selectedContentColor = activeColor,
                        unselectedContentColor = idleColor,
                        onClick = { viewModel.currentSection.value = "CRM" },
                        text = { Text("CRM", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        icon = { Icon(Icons.Default.People, "Contacts", modifier = Modifier.size(20.dp)) }
                    )
                }
            }
        }
    ) { pad ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepNavy)
                .padding(pad)
        ) {
            AnimatedContent(
                targetState = currentView,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "View Transition"
            ) { section ->
                when (section) {
                    "Dashboard" -> DashboardView(viewModel, accounts, transactions, invoices, taxRecords)
                    "Ledger" -> LedgerView(viewModel, accounts, transactions)
                    "Invoices" -> InvoicesView(viewModel, invoices, contacts)
                    "Expenses" -> ExpensesView(viewModel, expenses)
                    "Tax & AI" -> TaxAndAIView(viewModel, taxRecords, invoices, expenses, accounts)
                    "CRM" -> CRMView(viewModel, contacts)
                }
            }

            // Company Swapper dialog
            if (showCompanyDialog) {
                Dialog(onDismissRequest = { showCompanyDialog = false }) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                "Select Active Company",
                                fontWeight = FontWeight.Bold,
                                color = IceWhite,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            val companies = listOf(
                                "AeroTax Enterprise Ltd",
                                "Vanguard Contracting Group",
                                "Stark Industries Holdings",
                                "Acme Development Inc."
                            )
                            companies.forEach { company ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            activeCompany = company
                                            showCompanyDialog = false
                                        }
                                        .padding(vertical = 12.dp, horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Business,
                                        contentDescription = null,
                                        tint = if (company == activeCompany) EmeraldMint else SlateGrey,
                                        modifier = Modifier.padding(end = 12.dp)
                                    )
                                    Text(
                                        text = company,
                                        color = if (company == activeCompany) EmeraldMint else IceWhite,
                                        fontWeight = if (company == activeCompany) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 15.sp
                                    )
                                }
                                Divider(color = GridDashedLine)
                            }
                        }
                    }
                }
            }
        }
    }
}

// =================== SUB-VIEWS ===================

@Composable
fun DashboardView(
    viewModel: AccountingViewModel,
    accounts: List<Account>,
    transactions: List<Transaction>,
    invoices: List<Invoice>,
    taxRecords: List<TaxRecord>
) {
    val aiInsights by viewModel.aiInsights.collectAsState()
    val isGeneratingInsights by viewModel.isGeneratingInsights.collectAsState()

    // 1. Calculate dynamic accounting matrices
    val bankBalance = accounts.find { it.code == "1010" }?.balance ?: 0.0
    val outstandingInvoiceSum = invoices.filter { it.status == "Sent" }.sumOf { it.totalAmount }
    val paidInvoiceSum = invoices.filter { it.status == "Paid" }.sumOf { it.totalAmount }
    val totalRevenue = accounts.find { it.code == "4000" }?.balance ?: 0.0
    val taxLiability = accounts.find { it.code == "2200" }?.balance ?: 0.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Financial Overview",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = IceWhite
                    )
                    Text(
                        "Real-time corporate double-entry balance",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SlateGrey
                    )
                }
                IconButton(
                    onClick = { viewModel.loadAiInsights() },
                    modifier = Modifier
                        .background(DarkNavyGrey, RoundedCornerShape(12.dp))
                        .size(44.dp)
                ) {
                    Icon(Icons.Default.Refresh, "Refresh Books", tint = EmeraldMint)
                }
            }
        }

        // Metrics Grid (Calculated on actual database values)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(
                        title = "ZAR Bank Balance",
                        value = "R${String.format(Locale.US, "%,.2f", bankBalance)}",
                        subtitle = "1010 Current Account",
                        color = TealAccent,
                        icon = Icons.Default.AccountBalance,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Total Receivables",
                        value = "R${String.format(Locale.US, "%,.2f", outstandingInvoiceSum)}",
                        subtitle = "Outstanding bills",
                        color = SoftYellow,
                        icon = Icons.Default.Pending,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(
                        title = "Invoiced Sales (YTD)",
                        value = "R${String.format(Locale.US, "%,.2f", totalRevenue)}",
                        subtitle = "Consulting Pty Services",
                        color = EmeraldMint,
                        icon = Icons.Default.TrendingUp,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Estimated SARS VAT",
                        value = "R${String.format(Locale.US, "%,.2f", taxLiability)}",
                        subtitle = "SARS VAT 201 Liability",
                        color = WarmCoral,
                        icon = Icons.Default.Savings,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Beautiful Interactive Bezier Curves Chart modeled in Canvas (Fluidity design standard)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, GridDashedLine),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Operating Metrics (Income vs Expenses)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = IceWhite
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(EmeraldMint, RoundedCornerShape(4.dp)))
                            Text(" Revenue", fontSize = 11.sp, color = SlateGrey, modifier = Modifier.padding(end = 8.dp))
                            Box(modifier = Modifier.size(8.dp).background(WarmCoral, RoundedCornerShape(4.dp)))
                            Text(" Costs", fontSize = 11.sp, color = SlateGrey)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Bezier Drawing Canvas
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    ) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height

                        // Horizontal guide grid lines
                        for (i in 1..4) {
                            val y = canvasHeight * (i / 4f)
                            drawLine(
                                color = GridDashedLine,
                                start = Offset(0f, y),
                                end = Offset(canvasWidth, y),
                                strokeWidth = 1f
                            )
                        }

                        // Drawing Revenue (Emerald Teal plot)
                        val revenuePoints = listOf(0.15f, 0.3f, 0.25f, 0.45f, 0.6f, 0.85f)
                        val revenuePath = Path().apply {
                            moveTo(0f, canvasHeight * (1f - revenuePoints[0]))
                            for (index in 1 until revenuePoints.size) {
                                val x = canvasWidth * (index / (revenuePoints.size - 1f))
                                val y = canvasHeight * (1f - revenuePoints[index])
                                val prevX = canvasWidth * ((index - 1) / (revenuePoints.size - 1f))
                                val prevY = canvasHeight * (1f - revenuePoints[index - 1])
                                cubicTo(
                                    (prevX + x) / 2f, prevY,
                                    (prevX + x) / 2f, y,
                                    x, y
                                )
                            }
                        }
                        drawPath(
                            path = revenuePath,
                            color = EmeraldMint,
                            style = Stroke(width = 5f)
                        )

                        // Drawing Expenses (Warm Coral list plot)
                        val expensePoints = listOf(0.4f, 0.35f, 0.45f, 0.25f, 0.3f, 0.22f)
                        val expensePath = Path().apply {
                            moveTo(0f, canvasHeight * (1f - expensePoints[0]))
                            for (index in 1 until expensePoints.size) {
                                val x = canvasWidth * (index / (expensePoints.size - 1f))
                                val y = canvasHeight * (1f - expensePoints[index])
                                val prevX = canvasWidth * ((index - 1) / (expensePoints.size - 1f))
                                val prevY = canvasHeight * (1f - expensePoints[index - 1])
                                cubicTo(
                                    (prevX + x) / 2f, prevY,
                                    (prevX + x) / 2f, y,
                                    x, y
                                )
                            }
                        }
                        drawPath(
                            path = expensePath,
                            color = WarmCoral,
                            style = Stroke(width = 4f)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun").forEach { text ->
                            Text(text, fontSize = 11.sp, color = SlateGrey)
                        }
                    }
                }
            }
        }

        // Intelligent Automated CPA Advisor Panel (Gemini API integrated)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = BlueAccentDark),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = Color(0xFF60A5FA),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "AI Insight & Business Auditor",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                        if (isGeneratingInsights) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                        } else {
                            Button(
                                onClick = { viewModel.loadAiInsights() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = BlueAccentDark),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                                modifier = Modifier.height(30.dp),
                                shape = RoundedCornerShape(15.dp)
                            ) {
                                Text("Audit", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = aiInsights ?: "Ask Gemini to audit your accounts...",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 19.sp,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Recent Audit General Ledger Trail
        item {
            Text(
                "Recent Bookkeeping Logs",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = IceWhite,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (transactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No transactions logged yet.", color = SlateGrey)
                }
            }
        } else {
            items(transactions.take(5)) { tx ->
                LedgerItemRow(tx = tx, onDelete = { viewModel.deleteTransaction(tx) })
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, GridDashedLine),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = SlateGrey,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                color = IceWhite,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle.uppercase(),
                fontSize = 9.sp,
                color = SlateGrey.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

// ================== GENERAL LEDGER MODULE ==================

@Composable
fun LedgerView(
    viewModel: AccountingViewModel,
    accounts: List<Account>,
    transactions: List<Transaction>
) {
    var code by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Asset") }

    var txDesc by remember { mutableStateOf("") }
    var txDebitCode by remember { mutableStateOf("1010") }
    var txCreditCode by remember { mutableStateOf("4000") }
    var txAmount by remember { mutableStateOf("") }
    var txCategory by remember { mutableStateOf("Consulting") }
    var txTaxRate by remember { mutableStateOf("0.15") }

    var showAddAccountForm by remember { mutableStateOf(false) }
    var showAddTxForm by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("General Ledger", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = IceWhite)
                    Text("Charts of Accounts and Double-Entry Records", style = MaterialTheme.typography.bodyMedium, color = SlateGrey)
                }
            }
        }

        // Quick Command Buttons
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { showAddAccountForm = !showAddAccountForm },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkNavyGrey, contentColor = TealAccent),
                    border = BorderStroke(1.dp, TealAccent)
                ) {
                    Icon(Icons.Default.Add, null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Account Code", fontSize = 13.sp)
                }
                Button(
                    onClick = { showAddTxForm = !showAddTxForm },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldMint, contentColor = Color.Black)
                ) {
                    Icon(Icons.Default.PostAdd, null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Journal entry", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Add Account Expandable Form
        if (showAddAccountForm) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GridDashedLine)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Register Code to Chart of Accounts", fontWeight = FontWeight.Bold, color = IceWhite)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = code,
                                onValueChange = { code = it },
                                label = { Text("Code") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite)
                            )
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Name") },
                                modifier = Modifier.weight(2f),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite)
                            )
                        }
                        
                        // Select type dropdown trigger
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Category:", color = SlateGrey, fontSize = 14.sp)
                            val options = listOf("Asset", "Liability", "Equity", "Revenue", "Expense")
                            options.forEach { opt ->
                                Card(
                                    modifier = Modifier
                                        .clickable { type = opt }
                                        .border(
                                            1.dp,
                                            if (type == opt) EmeraldMint else Color.Transparent,
                                            RoundedCornerShape(8.dp)
                                        ),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (type == opt) DarkNavyGrey else ObsidianSurface
                                    )
                                ) {
                                    Text(opt, color = if (type == opt) EmeraldMint else SlateGrey, modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp), fontSize = 11.sp)
                                }
                            }
                        }

                        Button(
                            onClick = {
                                if (code.isNotBlank() && name.isNotBlank()) {
                                    viewModel.addAccount(code, name, type)
                                    code = ""
                                    name = ""
                                    showAddAccountForm = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldMint, contentColor = Color.Black),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Create Account", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Add Double Entry Journal Form
        if (showAddTxForm) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, TealAccent)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Record Double-Entry Bookkeeping Line", fontWeight = FontWeight.Bold, color = IceWhite)
                        OutlinedTextField(
                            value = txDesc,
                            onValueChange = { txDesc = it },
                            label = { Text("Transaction description") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = txDebitCode,
                                onValueChange = { txDebitCode = it },
                                label = { Text("Debit account (Asset/Exp)") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite)
                            )
                            OutlinedTextField(
                                value = txCreditCode,
                                onValueChange = { txCreditCode = it },
                                label = { Text("Credit account (Liab/Rev/Eq)") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite)
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = txAmount,
                                onValueChange = { txAmount = it },
                                label = { Text("Amount (ZAR R)") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite)
                            )
                            OutlinedTextField(
                                value = txCategory,
                                onValueChange = { txCategory = it },
                                label = { Text("Category tag") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite)
                            )
                        }

                        Button(
                            onClick = {
                                val amt = txAmount.toDoubleOrNull()
                                val tr = txTaxRate.toDoubleOrNull() ?: 0.15
                                if (txDesc.isNotBlank() && amt != null) {
                                    viewModel.addManualTransaction(txDesc, txDebitCode, txCreditCode, amt, txCategory, tr)
                                    txDesc = ""
                                    txAmount = ""
                                    showAddTxForm = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldMint, contentColor = Color.Black),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Post Journal Entries (Double-Balanced)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Text("Charts of Accounts Ledger Summary", fontWeight = FontWeight.Bold, color = IceWhite, fontSize = 16.sp)
        }

        // Interactive Accounts Table list
        items(accounts) { acc ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ObsidianSurface, RoundedCornerShape(8.dp))
                    .padding(14.dp)
                    .border(
                        border = BorderStroke(
                            1.dp,
                            if (acc.balance < 0 && (acc.type == "Asset" || acc.type == "Expense")) WarmCoral.copy(alpha = 0.5f) else Color.Transparent
                        )
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = when (acc.type) {
                                    "Asset" -> TealAccent.copy(alpha = 0.2f)
                                    "Liability" -> WarmCoral.copy(alpha = 0.2f)
                                    "Equity" -> SoftYellow.copy(alpha = 0.2f)
                                    "Revenue" -> EmeraldMint.copy(alpha = 0.2f)
                                    else -> SlateGrey.copy(alpha = 0.2f)
                                },
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(acc.code, color = IceWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(acc.name, fontWeight = FontWeight.Bold, color = IceWhite, fontSize = 14.sp)
                        Text(acc.type, color = SlateGrey, fontSize = 10.sp)
                    }
                }
                Text(
                    text = "R${String.format(Locale.US, "%,.2f", acc.balance)}",
                    fontWeight = FontWeight.ExtraBold,
                    color = if (acc.type == "Asset" && acc.balance >= 0) EmeraldMint else if (acc.type == "Expense") WarmCoral else IceWhite,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
fun LedgerItemRow(tx: Transaction, onDelete: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, GridDashedLine)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            if (tx.isExpense) WarmCoral.copy(alpha = 0.15f) else EmeraldMint.copy(alpha = 0.15f),
                            RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (tx.isExpense) Icons.Default.ArrowOutward else Icons.Default.CallReceived,
                        contentDescription = null,
                        tint = if (tx.isExpense) WarmCoral else EmeraldMint,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(tx.description, fontWeight = FontWeight.Bold, color = IceWhite, fontSize = 13.sp)
                    Text(
                        text = "Debit dr: ${tx.debitAccountCode} | Credit cr: ${tx.creditAccountCode}",
                        fontSize = 10.sp,
                        color = SlateGrey,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (tx.isExpense) "-" else "+"}R${String.format(Locale.US, "%,.2f", tx.amount)}",
                    fontWeight = FontWeight.Bold,
                    color = if (tx.isExpense) WarmCoral else EmeraldMint,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (tx.reconciled) Icons.Default.CheckCircle else Icons.Default.Info,
                        contentDescription = null,
                        tint = if (tx.reconciled) EmeraldMint else SoftYellow,
                        modifier = Modifier.size(10.dp)
                    )
                    Text(
                        text = if (tx.reconciled) " Reconciled" else " Unreconciled",
                        color = if (tx.reconciled) EmeraldMint else SoftYellow,
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}

// =================== INVOICING COMPONENT ===================

@Composable
fun InvoicesView(
    viewModel: AccountingViewModel,
    invoices: List<Invoice>,
    contacts: List<Contact>
) {
    var invoiceNumber by remember { mutableStateOf("INV-2026-004") }
    var selectedCustomer by remember { mutableStateOf("") }
    var customerEmail by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var itemDescription by remember { mutableStateOf("") }

    var showDraftForm by remember { mutableStateOf(false) }
    var selectedInvoiceForPreview by remember { mutableStateOf<Invoice?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Invoices Hub", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = IceWhite)
                    Text("Manage recurring accounts receivable bills & quotes", style = MaterialTheme.typography.bodyMedium, color = SlateGrey)
                }
            }
        }

        // Buttons row
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { showDraftForm = !showDraftForm },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldMint, contentColor = Color.Black)
                ) {
                    Icon(Icons.Default.Add, null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Create Corporate Invoice", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        // Dynamic Draft Invoice Form
        if (showDraftForm) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GridDashedLine)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Draft New Invoice Bill", fontWeight = FontWeight.Bold, color = IceWhite)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = invoiceNumber,
                                onValueChange = { invoiceNumber = it },
                                label = { Text("Invoice Number") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite)
                            )
                            OutlinedTextField(
                                value = selectedCustomer,
                                onValueChange = { selectedCustomer = it },
                                label = { Text("Customer") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite)
                            )
                        }
                        OutlinedTextField(
                            value = customerEmail,
                            onValueChange = { customerEmail = it },
                            label = { Text("Customer Email") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite)
                        )
                        OutlinedTextField(
                            value = itemDescription,
                            onValueChange = { itemDescription = it },
                            label = { Text("Consulting details / products") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite)
                        )
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            label = { Text("Net Total Price (including 15% VAT)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite)
                        )

                        Button(
                            onClick = {
                                val baseAmt = amount.toDoubleOrNull() ?: 0.0
                                val sub = baseAmt / 1.15
                                val tax = baseAmt - sub
                                if (selectedCustomer.isNotBlank() && baseAmt > 0) {
                                    viewModel.createInvoice(
                                        invoiceNumber = invoiceNumber,
                                        customerName = selectedCustomer,
                                        email = customerEmail,
                                        amount = baseAmt,
                                        subtotal = sub,
                                        tax = tax,
                                        items = itemDescription
                                    )
                                    // Reset fields
                                    amount = ""
                                    selectedCustomer = ""
                                    customerEmail = ""
                                    itemDescription = ""
                                    showDraftForm = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldMint, contentColor = Color.Black),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Post & Send Invoice (Register AR asset)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Active Invoices Table list
        items(invoices) { invoice ->
            Card(
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedInvoiceForPreview = invoice },
                border = BorderStroke(1.dp, GridDashedLine)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(invoice.invoiceNumber, fontWeight = FontWeight.Bold, color = IceWhite, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (invoice.status == "Paid") EmeraldMint.copy(alpha = 0.15f) else SoftYellow.copy(alpha = 0.15f),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    invoice.status,
                                    color = if (invoice.status == "Paid") EmeraldMint else SoftYellow,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text("Customer: ${invoice.customerName}", color = SlateGrey, fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "R${String.format(Locale.US, "%,.2f", invoice.totalAmount)}",
                            fontWeight = FontWeight.ExtraBold,
                            color = IceWhite,
                            fontFamily = FontFamily.Monospace
                        )
                        Text("SARS VAT (15%): R${String.format(Locale.US, "%,.2f", invoice.taxAmount)}", fontSize = 9.sp, color = SlateGrey)
                    }
                }
            }
        }
    }

    // Dynamic Branded Invoice Preview Dialog (Simulating client statement generation)
    selectedInvoiceForPreview?.let { inv ->
        Dialog(onDismissRequest = { selectedInvoiceForPreview = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, TealAccent),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("AEROTAX", fontWeight = FontWeight.ExtraBold, color = EmeraldMint, fontSize = 20.sp)
                            Text("Cloud Accounting Suite", fontSize = 10.sp, color = SlateGrey)
                        }
                        Box(
                            modifier = Modifier
                                .border(2.dp, if (inv.status == "Paid") EmeraldMint else SoftYellow, RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = inv.status.uppercase(),
                                color = if (inv.status == "Paid") EmeraldMint else SoftYellow,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("INVOICE STATEMENT", fontWeight = FontWeight.Bold, color = IceWhite, fontSize = 15.sp)
                    Divider(color = GridDashedLine)

                    // Corporate layout mapping
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("FROM:", fontSize = 9.sp, color = SlateGrey, fontWeight = FontWeight.Bold)
                            Text("AeroTax Africa (Pty) Ltd", fontSize = 11.sp, color = IceWhite)
                            Text("4 Maude St, Sandton, 2031", fontSize = 9.sp, color = SlateGrey)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("TO:", fontSize = 9.sp, color = SlateGrey, fontWeight = FontWeight.Bold)
                            Text(inv.customerName, fontSize = 11.sp, color = IceWhite)
                            Text(inv.email, fontSize = 9.sp, color = SlateGrey)
                        }
                    }

                    Divider(color = GridDashedLine)

                    // Details block
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("INVOICE DATE:", fontSize = 9.sp, color = SlateGrey)
                            val df = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                            Text(df.format(Date(inv.date)), fontSize = 11.sp, color = IceWhite)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("DUE DATE:", fontSize = 9.sp, color = SlateGrey)
                            val df = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                            Text(df.format(Date(inv.dueDate)), fontSize = 11.sp, color = IceWhite)
                        }
                    }

                    Divider(color = GridDashedLine)

                    // Items display Grid
                    Text("BILLABLE ITEMS:", fontSize = 10.sp, color = SlateGrey, fontWeight = FontWeight.Bold)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkNavyGrey),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            // Row Header
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Line Description", color = SlateGrey, fontSize = 11.sp, modifier = Modifier.weight(1.5f))
                                Text("Qty", color = SlateGrey, fontSize = 11.sp, modifier = Modifier.weight(0.5f), textAlign = TextAlign.Center)
                                Text("Price", color = SlateGrey, fontSize = 11.sp, modifier = Modifier.weight(0.8f), textAlign = TextAlign.End)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Divider(color = GridDashedLine)
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            // Splitting raw itemsJson
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(inv.itemsJson, color = IceWhite, fontSize = 12.sp, modifier = Modifier.weight(1.5f))
                                Text("1", color = IceWhite, fontSize = 12.sp, modifier = Modifier.weight(0.5f), textAlign = TextAlign.Center)
                                Text("R${String.format(Locale.US, "%,.2f", inv.subtotal)}", color = IceWhite, fontSize = 12.sp, modifier = Modifier.weight(0.8f), textAlign = TextAlign.End, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }

                    // Calculations
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(0.6f), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal:", color = SlateGrey, fontSize = 12.sp)
                            Text("R${String.format(Locale.US, "%,.2f", inv.subtotal)}", color = IceWhite, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        }
                        Row(modifier = Modifier.fillMaxWidth(0.6f), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("SARS VAT (15%):", color = SlateGrey, fontSize = 12.sp)
                            Text("R${String.format(Locale.US, "%,.2f", inv.taxAmount)}", color = IceWhite, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        }
                        Divider(color = GridDashedLine, modifier = Modifier.fillMaxWidth(0.6f))
                        Row(modifier = Modifier.fillMaxWidth(0.6f), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Net total amount:", fontWeight = FontWeight.Bold, color = IceWhite, fontSize = 14.sp)
                            Text("R${String.format(Locale.US, "%,.2f", inv.totalAmount)}", fontWeight = FontWeight.ExtraBold, color = EmeraldMint, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { selectedInvoiceForPreview = null },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SlateGrey),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Close Preview")
                        }
                        if (inv.status != "Paid") {
                            Button(
                                onClick = {
                                    viewModel.markInvoicePaid(inv)
                                    selectedInvoiceForPreview = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldMint, contentColor = Color.Black),
                                modifier = Modifier.weight(1.5f)
                            ) {
                                Icon(Icons.Default.CreditCard, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Register Payment", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ================= EXPENSES & OCR SYSTEM =================

@Composable
fun ExpensesView(
    viewModel: AccountingViewModel,
    expenses: List<Expense>
) {
    val ocrDraft by viewModel.ocrDraft.collectAsState()
    val isOcrLoading by viewModel.isOcrLoading.collectAsState()

    var manualMerchant by remember { mutableStateOf("") }
    var manualAmount by remember { mutableStateOf("") }
    var manualCategory by remember { mutableStateOf("SaaS") }
    
    // Receipt prompt scanning
    var ocrRawText by remember { mutableStateOf("") }

    // Mileage variables
    var showMileageForm by remember { mutableStateOf(false) }
    var milesTraveled by remember { mutableStateOf("") }
    var tripReason by remember { mutableStateOf("") }

    var showReceiptScanner by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Expenses Center", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = IceWhite)
                    Text("OCR intelligent receipt scanning and smart categorization", style = MaterialTheme.typography.bodyMedium, color = SlateGrey)
                }
            }
        }

        // Action Buttons Row
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { showReceiptScanner = !showReceiptScanner },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldMint, contentColor = Color.Black)
                ) {
                    Icon(Icons.Default.DocumentScanner, null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Simulate AI OCR Scan", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Button(
                    onClick = { showMileageForm = !showMileageForm },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkNavyGrey, contentColor = TealAccent),
                    border = BorderStroke(1.dp, TealAccent)
                ) {
                    Icon(Icons.Default.DirectionsCar, null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Track Mileage", fontSize = 13.sp)
                }
            }
        }

        // Preloaded Crumpled receipt logs for quick testing
        if (showReceiptScanner) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, TealAccent)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("AI Intelligent Receipt Snapper", fontWeight = FontWeight.Bold, color = IceWhite)
                        Text(
                            "Type or simulate crumpled receipt tickets below to let Gemini analyze deductibles and compile fields into your ledger automatically.",
                            fontSize = 11.sp,
                            color = SlateGrey
                        )

                        // Simulated Snippet Insertions (EXCELLENT UX)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    ocrRawText = "MTN SA LTE BUSINESS FIBRE TAX INVOICE\nDate: 2026-05-18\nInvoice #: MTN-09384\nSubtotal: R4130.43\nSARS VAT (15%): R619.57\nTotal Paid: R4750.00 ZAR\nEFT Reference: SEC9910"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkNavyGrey, contentColor = TealAccent),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text("MTN Fibre Invoice", fontSize = 11.sp)
                            }
                            Button(
                                onClick = {
                                    ocrRawText = "MUGG & BEAN\nSandton City Shopping Centre\nDate: 05/19/2026\n1x Café Latte: R45.00\n2x Ranch Breakfast: R180.00\nSubtotal: R225.00\nSARS VAT (15%): R33.75\nTOTAL: R258.75 ZAR"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkNavyGrey, contentColor = TealAccent),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text("M&B Breakfast Ticket", fontSize = 11.sp)
                            }
                        }

                        OutlinedTextField(
                            value = ocrRawText,
                            onValueChange = { ocrRawText = it },
                            label = { Text("Receipt raw OCR print (Markdown or Text)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite)
                        )

                        if (isOcrLoading) {
                            Box(modifier = Modifier.fillMaxWidth().height(48.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = EmeraldMint)
                            }
                        } else {
                            Button(
                                onClick = {
                                    if (ocrRawText.isNotBlank()) {
                                        viewModel.runOcrScanning(ocrRawText)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldMint, contentColor = Color.Black),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Trigger Gemini API Parsing", fontWeight = FontWeight.Bold)
                            }
                        }

                        // Displays Gemini extracted JSON draft
                        ocrDraft?.let { json ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = DarkNavyGrey),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.padding(top = 10.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("Gemini Extracted Schema Details:", fontWeight = FontWeight.Bold, color = EmeraldMint, fontSize = 13.sp)
                                    Text("Merchant: ${json.optString("merchant")}", color = IceWhite, fontSize = 13.sp)
                                    Text("Amount parsed: R${json.optDouble("amount")}", color = IceWhite, fontSize = 13.sp)
                                    Text("Suggested category: ${json.optString("category")}", color = IceWhite, fontSize = 13.sp)
                                    Text("Detected date: ${json.optString("date")}", color = SlateGrey, fontSize = 12.sp)

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Button(
                                            onClick = {
                                                val merchant = json.optString("merchant", "Extracted Spot")
                                                val amt = json.optDouble("amount", 0.0)
                                                val cat = json.optString("category", "General")
                                                viewModel.addManualExpense(merchant, amt, cat, ocrRawText)
                                                viewModel.clearOcrDraft()
                                                ocrRawText = ""
                                                showReceiptScanner = false
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldMint, contentColor = Color.Black),
                                            modifier = Modifier.weight(1.5f)
                                        ) {
                                            Text("Approve & Post Ledger", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                        OutlinedButton(
                                            onClick = { viewModel.clearOcrDraft() },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Reject", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Mileage Expense Calculator Form
        if (showMileageForm) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GridDashedLine)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Log Travel & Auto SARS Deductions", fontWeight = FontWeight.Bold, color = IceWhite)
                        OutlinedTextField(
                            value = milesTraveled,
                            onValueChange = { milesTraveled = it },
                            label = { Text("Kilometers traveled") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite)
                        )
                        OutlinedTextField(
                            value = tripReason,
                            onValueChange = { tripReason = it },
                            label = { Text("Client visit details") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("SARS Rate per Kilometer: R4.84/km", fontSize = 11.sp, color = SlateGrey)
                            val d = milesTraveled.toDoubleOrNull() ?: 0.0
                            val calcDeduction = d * 4.84
                            Text("Est SARS Deduction: R${String.format(Locale.US, "%,.2f", calcDeduction)}", color = EmeraldMint, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val distance = milesTraveled.toDoubleOrNull()
                                if (distance != null && tripReason.isNotBlank()) {
                                    val calcTotal = distance * 4.84
                                    viewModel.addManualExpense("Travel: $tripReason", calcTotal, "Meals", "Travel trip recorded for distance $distance km")
                                    milesTraveled = ""
                                    tripReason = ""
                                    showMileageForm = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldMint, contentColor = Color.Black),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Post Travel business deduction to books", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Form to add manual expenses
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, GridDashedLine)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Record Quick Business cost Outflow", fontWeight = FontWeight.Bold, color = IceWhite)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = manualMerchant,
                            onValueChange = { manualMerchant = it },
                            label = { Text("Supplier Shop") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite)
                        )
                        OutlinedTextField(
                            value = manualAmount,
                            onValueChange = { manualAmount = it },
                            label = { Text("Cash spent") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite)
                        )
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Category:", color = SlateGrey, fontSize = 13.sp)
                        val categories = listOf("SaaS", "Meals", "Rent", "Taxes")
                        categories.forEach { cat ->
                            Card(
                                modifier = Modifier
                                    .clickable { manualCategory = cat }
                                    .border(
                                        1.dp,
                                        if (manualCategory == cat) EmeraldMint else Color.Transparent,
                                        RoundedCornerShape(8.dp)
                                    ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (manualCategory == cat) DarkNavyGrey else ObsidianSurface
                                )
                            ) {
                                Text(cat, color = if (manualCategory == cat) EmeraldMint else SlateGrey, modifier = Modifier.padding(vertical = 4.dp, horizontal = 10.dp), fontSize = 12.sp)
                            }
                        }
                    }

                    Button(
                        onClick = {
                            val amt = manualAmount.toDoubleOrNull()
                            if (manualMerchant.isNotBlank() && amt != null) {
                                viewModel.addManualExpense(manualMerchant, amt, manualCategory)
                                manualMerchant = ""
                                manualAmount = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldMint, contentColor = Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Post expense bill", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text("Logged business outlays list", fontWeight = FontWeight.Bold, color = IceWhite, fontSize = 16.sp)
        }

        // List logged expenses
        items(expenses) { exp ->
            Card(
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, GridDashedLine)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(exp.merchant, fontWeight = FontWeight.Bold, color = IceWhite, fontSize = 14.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .background(DarkNavyGrey, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(exp.category, color = TealAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            if (exp.isScanned) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.AutoAwesome, "Extracted by AI", tint = EmeraldMint, modifier = Modifier.size(12.dp))
                                Text(" AI Verified", fontSize = 9.sp, color = EmeraldMint)
                            }
                        }
                    }
                    Text(
                        "-R${String.format(Locale.US, "%,.2f", exp.amount)}",
                        fontWeight = FontWeight.ExtraBold,
                        color = WarmCoral,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

// ============= TAXES COMPLIANCE & AI ADVISOR INTERACTIVE CHAT =============

@Composable
fun TaxAndAIView(
    viewModel: AccountingViewModel,
    taxRecords: List<TaxRecord>,
    invoices: List<Invoice>,
    expenses: List<Expense>,
    accounts: List<Account>
) {
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isChatLoading by viewModel.isChatLoading.collectAsState()
    var inputMessage by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    // 1. Double calculations math (VAT Return details, corporate tax estimation)
    val totalRevenue = accounts.find { it.code == "4000" }?.balance ?: 0.0
    val totalCosts = accounts.filter { it.type == "Expense" }.sumOf { it.balance }
    val netProfit = totalRevenue - totalCosts
    val corpTaxEst = if (netProfit > 0) netProfit * 0.27 else 0.0 // Standard 27% SA corporate rate

    val outstandingVATRec = taxRecords.filter { !it.isFiled && it.taxType == "VAT" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text("Compliance Office & Assistant", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = IceWhite)
                Text("Generate tax estimations, lock filing periods, and consult AI CPA advisors", style = MaterialTheme.typography.bodyMedium, color = SlateGrey)
            }
        }

        // Live Corporate Calculations Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, GridDashedLine)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                     Text("SARS Corporate Income Tax (CIT) Estimations", fontWeight = FontWeight.Bold, color = IceWhite, fontSize = 14.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("YTD Revenues (Sales Account):", color = SlateGrey, fontSize = 13.sp)
                        Text("R${String.format(Locale.US, "%,.2f", totalRevenue)}", color = EmeraldMint, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("YTD Safe Business Deductions:", color = SlateGrey, fontSize = 13.sp)
                        Text("R${String.format(Locale.US, "%,.2f", totalCosts)}", color = WarmCoral, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Divider(color = GridDashedLine)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Current Operating Profit Margin:", color = SlateGrey, fontSize = 13.sp)
                        val margin = if (totalRevenue > 0) (netProfit / totalRevenue) * 100 else 0.0
                        Text("${String.format(Locale.US, "%.1f", margin)}%", color = TealAccent, fontWeight = FontWeight.ExtraBold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("SARS Corporate CIT Estimate (27% flat rate):", color = SlateGrey, fontSize = 13.sp)
                        Text("R${String.format(Locale.US, "%,.2f", corpTaxEst)}", color = SoftYellow, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }

        // IRS & VAT Active Lock & Filing List
        item {
            Text("Active SARS & CIPC Compliance Returns", fontWeight = FontWeight.Bold, color = IceWhite, fontSize = 16.sp)
        }

        items(taxRecords) { record ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ObsidianSurface, RoundedCornerShape(8.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(record.period, fontWeight = FontWeight.Bold, color = IceWhite, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    if (record.isFiled) EmeraldMint.copy(alpha = 0.15f) else SoftYellow.copy(alpha = 0.15f),
                                    RoundedCornerShape(4.dp)
                                    )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                if (record.isFiled) "Filed" else "Draft Lock",
                                color = if (record.isFiled) EmeraldMint else SoftYellow,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text("Tax classification: ${record.taxType}", color = SlateGrey, fontSize = 11.sp)
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "R${String.format(Locale.US, "%,.2f", record.calculatedAmount)}",
                        color = if (record.isFiled) EmeraldMint else SoftYellow,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    if (!record.isFiled) {
                        Button(
                            onClick = { viewModel.fileTaxPeriod(record) },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldMint, contentColor = Color.Black),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.height(26.dp)
                        ) {
                            Text("Submit Return", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Chat Conversation Module
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, TealAccent)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.SmartButton, null, tint = EmeraldMint, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SARS & CIPC Virtual CPA Advisor", fontWeight = FontWeight.Bold, color = IceWhite)
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    // Feed list
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .background(DeepNavy, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        chatMessages.forEach { (text, isAi) ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp),
                                contentAlignment = if (isAi) Alignment.CenterStart else Alignment.CenterEnd
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isAi) DarkNavyGrey else TealAccent
                                    ),
                                    shape = RoundedCornerShape(
                                        topStart = 12.dp,
                                        topEnd = 12.dp,
                                        bottomStart = if (isAi) 0.dp else 12.dp,
                                        bottomEnd = if (isAi) 12.dp else 0.dp
                                    ),
                                    modifier = Modifier.fillMaxWidth(0.85f)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = if (isAi) "AeroTax CPA Agent" else "You (Owner)",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isAi) EmeraldMint else Color.Black
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = text,
                                            fontSize = 12.sp,
                                            color = if (isAi) IceWhite else Color.Black,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }

                        if (isChatLoading) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), color = EmeraldMint, strokeWidth = 1.5.dp)
                                Text(" CPA Agent formulating response...", fontSize = 11.sp, color = SlateGrey)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Input Form row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputMessage,
                            onValueChange = { inputMessage = it },
                            placeholder = { Text("Query optimizing, deductibles, etc...", fontSize = 12.sp) },
                            modifier = Modifier.weight(1.5f),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite)
                        )
                        IconButton(
                            onClick = {
                                if (inputMessage.isNotBlank()) {
                                    viewModel.postChatMessage(inputMessage)
                                    inputMessage = ""
                                }
                            },
                            modifier = Modifier
                                .background(EmeraldMint, RoundedCornerShape(12.dp))
                                .size(44.dp)
                        ) {
                            Icon(Icons.Default.Send, "Send prompt text", tint = Color.Black)
                        }
                    }
                }
            }
        }
    }
}

// ======================= CRM AND CONTACT SHEET =======================

@Composable
fun CRMView(
    viewModel: AccountingViewModel,
    contacts: List<Contact>
) {
    var cName by remember { mutableStateOf("") }
    var cEmail by remember { mutableStateOf("") }
    var cPhone by remember { mutableStateOf("") }
    var cIsCustomer by remember { mutableStateOf(true) }

    var showContactForm by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text("Contacts Database", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = IceWhite)
                Text("Manage customer relationships and vendor supply details safely", style = MaterialTheme.typography.bodyMedium, color = SlateGrey)
            }
        }

        item {
            Button(
                onClick = { showContactForm = !showContactForm },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldMint, contentColor = Color.Black)
            ) {
                Icon(Icons.Default.PersonAdd, null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Corporate Contact", fontWeight = FontWeight.Bold)
            }
        }

        if (showContactForm) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GridDashedLine)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Add New Corporate Contact", fontWeight = FontWeight.Bold, color = IceWhite)
                        OutlinedTextField(
                            value = cName,
                            onValueChange = { cName = it },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite)
                        )
                        OutlinedTextField(
                            value = cEmail,
                            onValueChange = { cEmail = it },
                            label = { Text("Email identifier") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite)
                        )
                        OutlinedTextField(
                            value = cPhone,
                            onValueChange = { cPhone = it },
                            label = { Text("Phone coordinates") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = IceWhite, unfocusedTextColor = IceWhite)
                        )

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Type tag:", color = SlateGrey, fontSize = 13.sp)
                            listOf(true, false).forEach { isCust ->
                                Card(
                                    modifier = Modifier
                                        .clickable { cIsCustomer = isCust }
                                        .border(
                                            1.dp,
                                            if (cIsCustomer == isCust) EmeraldMint else Color.Transparent,
                                            RoundedCornerShape(8.dp)
                                        ),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (cIsCustomer == isCust) DarkNavyGrey else ObsidianSurface
                                    )
                                ) {
                                    Text(
                                        if (isCust) "Customer" else "Supplier Vendor",
                                        color = if (cIsCustomer == isCust) EmeraldMint else SlateGrey,
                                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 10.dp),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                if (cName.isNotBlank() && cEmail.isNotBlank()) {
                                    viewModel.addContact(cName, cEmail, cPhone, cIsCustomer, !cIsCustomer)
                                    cName = ""
                                    cEmail = ""
                                    cPhone = ""
                                    showContactForm = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldMint, contentColor = Color.Black),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Contact Record", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Text("Registered corporate directories", fontWeight = FontWeight.Bold, color = IceWhite, fontSize = 16.sp)
        }

        items(contacts) { contact ->
            Card(
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, GridDashedLine)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(contact.name, fontWeight = FontWeight.Bold, color = IceWhite, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (contact.isCustomer) TealAccent.copy(alpha = 0.15f) else SoftYellow.copy(alpha = 0.15f),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    if (contact.isCustomer) "Customer" else "Vendor",
                                    color = if (contact.isCustomer) TealAccent else SoftYellow,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text("Email: ${contact.email}", color = SlateGrey, fontSize = 11.sp)
                        if (contact.phone.isNotBlank()) {
                            Text("Phone: ${contact.phone}", color = SlateGrey, fontSize = 11.sp)
                        }
                    }
                    
                    if (contact.balance != 0.0) {
                        Text(
                            text = "R${String.format(Locale.US, "%,.2f", contact.balance)}",
                            fontWeight = FontWeight.Bold,
                            color = if (contact.isCustomer) EmeraldMint else WarmCoral,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
