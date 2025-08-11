// Autocomplete functionality for customers and books
function setupAutocomplete(inputId, type, onSelectCallback) {
    const input = document.getElementById(inputId);
    const suggestionsDiv = document.createElement('div');
    suggestionsDiv.className = 'autocomplete-suggestions';
    suggestionsDiv.style.display = 'none';
    
    input.parentNode.classList.add('autocomplete-container');
    input.parentNode.appendChild(suggestionsDiv);
    
    let selectedIndex = -1;
    let suggestions = [];
    
    input.addEventListener('input', function() {
        const searchTerm = this.value.trim();
        
        if (searchTerm.length < 2) {
            hideSuggestions();
            return;
        }
        
        fetch(`autocomplete?type=${type}&term=${encodeURIComponent(searchTerm)}`)
            .then(response => response.json())
            .then(data => {
                suggestions = data;
                showSuggestions(suggestions);
            })
            .catch(error => {
                console.error('Error fetching suggestions:', error);
                hideSuggestions();
            });
    });
    
    input.addEventListener('keydown', function(e) {
        if (suggestionsDiv.style.display === 'none') return;
        
        switch(e.key) {
            case 'ArrowDown':
                e.preventDefault();
                selectedIndex = Math.min(selectedIndex + 1, suggestions.length - 1);
                updateSelection();
                break;
            case 'ArrowUp':
                e.preventDefault();
                selectedIndex = Math.max(selectedIndex - 1, -1);
                updateSelection();
                break;
            case 'Enter':
                e.preventDefault();
                if (selectedIndex >= 0 && suggestions[selectedIndex]) {
                    selectSuggestion(suggestions[selectedIndex]);
                }
                break;
            case 'Escape':
                hideSuggestions();
                break;
        }
    });
    
    function showSuggestions(suggestions) {
        suggestionsDiv.innerHTML = '';
        selectedIndex = -1;
        
        if (suggestions.length === 0) {
            hideSuggestions();
            return;
        }
        
        suggestions.forEach((suggestion, index) => {
            const div = document.createElement('div');
            div.className = 'autocomplete-suggestion';
            
            if (type === 'customer') {
                div.innerHTML = `<strong>${suggestion.name}</strong><br><small>${suggestion.accountNumber} - ${suggestion.telephone}</small>`;
            } else if (type === 'book') {
                div.innerHTML = `<strong>${suggestion.title}</strong><br><small>by ${suggestion.author} - Rs. ${suggestion.price}</small>`;
            }
            
            div.addEventListener('click', () => selectSuggestion(suggestion));
            suggestionsDiv.appendChild(div);
        });
        
        suggestionsDiv.style.display = 'block';
    }
    
    function hideSuggestions() {
        suggestionsDiv.style.display = 'none';
        selectedIndex = -1;
    }
    
    function updateSelection() {
        const suggestions = suggestionsDiv.querySelectorAll('.autocomplete-suggestion');
        suggestions.forEach((suggestion, index) => {
            suggestion.classList.toggle('active', index === selectedIndex);
        });
    }
    
    function selectSuggestion(suggestion) {
        if (type === 'customer') {
            input.value = suggestion.name;
        } else if (type === 'book') {
            input.value = suggestion.title;
        }
        
        hideSuggestions();
        
        if (onSelectCallback) {
            onSelectCallback(suggestion);
        }
    }
    
    // Hide suggestions when clicking outside
    document.addEventListener('click', function(e) {
        if (!input.contains(e.target) && !suggestionsDiv.contains(e.target)) {
            hideSuggestions();
        }
    });
}

// Bill calculation functions
function calculateLineTotal(row) {
    const quantity = parseFloat(row.querySelector('.quantity').value || 0);
    const price = parseFloat(row.querySelector('.price').value || 0);
    const taxRate = parseFloat(row.querySelector('.tax').value || 0);
    const discountRate = parseFloat(row.querySelector('.discount').value || 0);
    
    const subtotal = quantity * price;
    const discountAmount = subtotal * (discountRate / 100);
    const afterDiscount = subtotal - discountAmount;
    const taxAmount = afterDiscount * (taxRate / 100);
    const total = afterDiscount + taxAmount;
    
    row.querySelector('.amount').value = total.toFixed(2);
    
    calculateBillTotal();
}

function calculateBillTotal() {
    const rows = document.querySelectorAll('.bill-item-row');
    let billSubtotal = 0;
    let billTaxAmount = 0;
    let billDiscountAmount = 0;
    let billTotal = 0;
    
    rows.forEach(row => {
        const quantity = parseFloat(row.querySelector('.quantity').value || 0);
        const price = parseFloat(row.querySelector('.price').value || 0);
        const taxRate = parseFloat(row.querySelector('.tax').value || 0);
        const discountRate = parseFloat(row.querySelector('.discount').value || 0);
        
        const subtotal = quantity * price;
        const discountAmount = subtotal * (discountRate / 100);
        const afterDiscount = subtotal - discountAmount;
        const taxAmount = afterDiscount * (taxRate / 100);
        const total = afterDiscount + taxAmount;
        
        billSubtotal += subtotal;
        billDiscountAmount += discountAmount;
        billTaxAmount += taxAmount;
        billTotal += total;
    });
    
    document.getElementById('billSubtotal').textContent = billSubtotal.toFixed(2);
    document.getElementById('billTaxAmount').textContent = billTaxAmount.toFixed(2);
    document.getElementById('billDiscountAmount').textContent = billDiscountAmount.toFixed(2);
    document.getElementById('billTotal').textContent = billTotal.toFixed(2);
}

// Add new bill item row
function addBillItemRow() {
    const tbody = document.querySelector('#billItemsTable tbody');
    const rowCount = tbody.querySelectorAll('tr').length + 1;
    
    const row = document.createElement('tr');
    row.className = 'bill-item-row';
    row.innerHTML = `
        <td>
            <input type="text" class="form-control item-input" name="item_${rowCount}" required>
            <input type="hidden" class="book-id" name="book_id_${rowCount}">
        </td>
        <td><input type="number" class="form-control price" name="price_${rowCount}" step="0.01" min="0" readonly></td>
        <td><input type="number" class="form-control quantity" name="quantity_${rowCount}" min="1" value="1" required></td>
        <td><input type="number" class="form-control tax" name="tax_${rowCount}" step="0.01" min="0" max="100" value="0"></td>
        <td><input type="number" class="form-control discount" name="discount_${rowCount}" step="0.01" min="0" max="100" value="0"></td>
        <td><input type="text" class="form-control amount" name="amount_${rowCount}" readonly></td>
        <td><button type="button" class="btn btn-danger btn-sm" onclick="removeRow(this)">Remove</button></td>
    `;
    
    tbody.appendChild(row);
    
    // Setup autocomplete for the new item input
    const itemInput = row.querySelector('.item-input');
    setupAutocomplete(itemInput.id = `item_${rowCount}`, 'book', function(book) {
        row.querySelector('.book-id').value = book.id;
        row.querySelector('.price').value = book.price;
        calculateLineTotal(row);
    });
    
    // Add event listeners for calculations
    row.querySelector('.quantity').addEventListener('input', () => calculateLineTotal(row));
    row.querySelector('.tax').addEventListener('input', () => calculateLineTotal(row));
    row.querySelector('.discount').addEventListener('input', () => calculateLineTotal(row));
    
    return row;
}

function removeRow(button) {
    button.closest('tr').remove();
    calculateBillTotal();
}

// Print bill function
function printBill() {
    window.print();
}

// Form validation
function validateBillForm() {
    const customerSelect = document.getElementById('customerId');
    const billItems = document.querySelectorAll('.bill-item-row');
    
    if (!customerSelect.value) {
        alert('Please select a customer');
        return false;
    }
    
    if (billItems.length === 0) {
        alert('Please add at least one item to the bill');
        return false;
    }
    
    for (let row of billItems) {
        const bookId = row.querySelector('.book-id').value;
        const quantity = row.querySelector('.quantity').value;
        
        if (!bookId) {
            alert('Please select a valid book for all items');
            return false;
        }
        
        if (!quantity || quantity <= 0) {
            alert('Please enter valid quantities for all items');
            return false;
        }
    }
    
    return true;
}

// Show loading spinner
function showLoading() {
    const button = document.querySelector('button[type="submit"]');
    button.disabled = true;
    button.innerHTML = 'Creating Bill...';
}

// Hide loading spinner
function hideLoading() {
    const button = document.querySelector('button[type="submit"]');
    button.disabled = false;
    button.innerHTML = 'Create Bill';
}

// Show success message
function showAlert(message, type = 'success') {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} fade-in`;
    alertDiv.textContent = message;
    
    const container = document.querySelector('.main-content');
    container.insertBefore(alertDiv, container.firstChild);
    
    setTimeout(() => {
        alertDiv.remove();
    }, 5000);
}