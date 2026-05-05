/**
 * MediStore - Order Management JS
 * Handles: search filtering, status/payment/date filters, pagination
 */

document.addEventListener('DOMContentLoaded', () => {

    /* ── Element refs ──────────────────────────────── */
    const searchInput     = document.getElementById('searchInput');
    const statusFilter    = document.getElementById('statusFilter');
    const paymentFilter   = document.getElementById('paymentFilter');
    const dateFilter      = document.getElementById('dateFilter');
    const tableBody       = document.getElementById('ordersTableBody');
    const showingInfo     = document.getElementById('showingInfo');
    const paginationEl    = document.getElementById('pagination');

    /* ── Config ────────────────────────────────────── */
    const ROWS_PER_PAGE = 5;
    let currentPage = 1;

    /* ── Helper: get all rows as array ────────────── */
    const getAllRows = () => Array.from(tableBody.querySelectorAll('tr[data-row]'));

    /* ── Filter logic ──────────────────────────────── */
    function applyFilters() {
        const query   = searchInput.value.trim().toLowerCase();
        const status  = statusFilter.value.toLowerCase();
        const payment = paymentFilter.value.toLowerCase();

        const rows = getAllRows();

        rows.forEach(row => {
            const text        = row.textContent.toLowerCase();
            const rowStatus   = (row.dataset.status  || '').toLowerCase();
            const rowPayment  = (row.dataset.payment || '').toLowerCase();

            const matchSearch  = !query   || text.includes(query);
            const matchStatus  = !status  || rowStatus  === status;
            const matchPayment = !payment || rowPayment === payment;

            row.dataset.visible = (matchSearch && matchStatus && matchPayment) ? 'true' : 'false';
        });

        currentPage = 1;
        renderPage();
    }

    /* ── Pagination render ─────────────────────────── */
    function renderPage() {
        const rows        = getAllRows();
        const visibleRows = rows.filter(r => r.dataset.visible !== 'false');
        const total       = visibleRows.length;
        const totalPages  = Math.max(1, Math.ceil(total / ROWS_PER_PAGE));

        // Clamp page
        if (currentPage > totalPages) currentPage = totalPages;

        const start = (currentPage - 1) * ROWS_PER_PAGE;
        const end   = start + ROWS_PER_PAGE;

        // Show/hide rows
        rows.forEach(r => r.style.display = 'none');
        visibleRows.slice(start, end).forEach(r => r.style.display = '');

        // Update "Showing X–Y of Z"
        if (showingInfo) {
            const from = total === 0 ? 0 : start + 1;
            const to   = Math.min(end, total);
            showingInfo.innerHTML = `Showing <strong>${from}–${to}</strong> of <strong>${total}</strong> orders`;
        }

        // Rebuild pagination buttons
        buildPagination(totalPages);
    }

    /* ── Build pagination buttons ──────────────────── */
    function buildPagination(totalPages) {
        if (!paginationEl) return;
        paginationEl.innerHTML = '';

        const addBtn = (label, page, isActive = false, isDisabled = false) => {
            const btn = document.createElement('div');
            btn.className = 'page-btn' + (isActive ? ' active' : '');
            btn.innerHTML = label;
            if (!isDisabled && !isActive) {
                btn.addEventListener('click', () => {
                    currentPage = page;
                    renderPage();
                });
            }
            paginationEl.appendChild(btn);
        };

        // Prev
        const prevBtn = document.createElement('div');
        prevBtn.className = 'page-btn';
        prevBtn.innerHTML = '<i class="fas fa-chevron-left" style="font-size:10px;"></i>';
        prevBtn.addEventListener('click', () => {
            if (currentPage > 1) { currentPage--; renderPage(); }
        });
        paginationEl.appendChild(prevBtn);

        // Page numbers with ellipsis
        const pages = buildPageNumbers(currentPage, totalPages);
        pages.forEach(p => {
            if (p === '…') {
                const ellipsis = document.createElement('div');
                ellipsis.className = 'page-btn';
                ellipsis.textContent = '…';
                paginationEl.appendChild(ellipsis);
            } else {
                addBtn(p, p, p === currentPage);
            }
        });

        // Next
        const nextBtn = document.createElement('div');
        nextBtn.className = 'page-btn';
        nextBtn.innerHTML = '<i class="fas fa-chevron-right" style="font-size:10px;"></i>';
        nextBtn.addEventListener('click', () => {
            if (currentPage < totalPages) { currentPage++; renderPage(); }
        });
        paginationEl.appendChild(nextBtn);
    }

    /* ── Smart page-number list ────────────────────── */
    function buildPageNumbers(current, total) {
        if (total <= 7) return Array.from({ length: total }, (_, i) => i + 1);
        const pages = [];
        if (current <= 4) {
            pages.push(1, 2, 3, 4, 5, '…', total);
        } else if (current >= total - 3) {
            pages.push(1, '…', total - 4, total - 3, total - 2, total - 1, total);
        } else {
            pages.push(1, '…', current - 1, current, current + 1, '…', total);
        }
        return pages;
    }

    /* ── Event listeners ───────────────────────────── */
    searchInput  ?.addEventListener('input',  applyFilters);
    statusFilter ?.addEventListener('change', applyFilters);
    paymentFilter?.addEventListener('change', applyFilters);
    dateFilter   ?.addEventListener('change', applyFilters);

    /* ── Initial render ────────────────────────────── */
    // Mark all rows as visible by default
    getAllRows().forEach(r => r.dataset.visible = 'true');
    renderPage();

    /* ── Row action handlers (delegate) ───────────── */
    tableBody.addEventListener('click', e => {
        const btn = e.target.closest('[data-action]');
        if (!btn) return;
        const row    = btn.closest('tr');
        const orderId = row?.querySelector('.order-id span')?.textContent?.trim();
        const action  = btn.dataset.action;

        if (action === 'view') {
            console.log('View order:', orderId);
            // e.g. window.location.href = `/admin/orders/${orderId}`;
        }
        if (action === 'edit') {
            console.log('Edit order:', orderId);
            // e.g. window.location.href = `/admin/orders/${orderId}/edit`;
        }
        if (action === 'cancel') {
            if (confirm(`Cancel order ${orderId}?`)) {
                console.log('Cancel order:', orderId);
                // e.g. fetch(`/admin/orders/${orderId}/cancel`, { method: 'POST' })
            }
        }
    });

});