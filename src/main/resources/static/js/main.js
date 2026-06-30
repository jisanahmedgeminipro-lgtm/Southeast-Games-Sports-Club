/* ==========================================================================
   SEU Games & Sports Club - Frontend interactions
   Loader, theme toggle, scroll progress, back-to-top, navbar, mobile menu,
   AOS / GSAP / Swiper init, animated counters, reveal fallback, OTP UX.
   No business logic - presentation only.
   ========================================================================== */
(function () {
    "use strict";

    /* ----------------------- Theme (set early elsewhere too) ----------------------- */
    const THEME_KEY = "seu-theme";
    function applyTheme(theme) {
        document.documentElement.setAttribute("data-theme", theme);
        try { localStorage.setItem(THEME_KEY, theme); } catch (e) {}
    }
    function initTheme() {
        let theme;
        try { theme = localStorage.getItem(THEME_KEY); } catch (e) {}
        if (!theme) {
            theme = window.matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light";
        }
        applyTheme(theme);
    }
    initTheme();

    document.addEventListener("DOMContentLoaded", function () {

        /* ----------------------- Loader ----------------------- */
        const loader = document.querySelector(".loader");
        if (loader) {
            window.addEventListener("load", function () {
                setTimeout(() => loader.classList.add("is-hidden"), 350);
            });
            // Safety: hide after 3.5s no matter what
            setTimeout(() => loader.classList.add("is-hidden"), 3500);
        }

        /* ----------------------- Theme toggle button ----------------------- */
        document.querySelectorAll("[data-theme-toggle]").forEach((btn) => {
            btn.addEventListener("click", function () {
                const next = document.documentElement.getAttribute("data-theme") === "dark" ? "light" : "dark";
                applyTheme(next);
            });
        });

        /* ----------------------- Navbar scroll state ----------------------- */
        const navbar = document.querySelector(".navbar");
        const onScrollNav = () => {
            if (!navbar) return;
            navbar.classList.toggle("is-scrolled", window.scrollY > 24);
        };
        onScrollNav();
        window.addEventListener("scroll", onScrollNav, { passive: true });

        /* ----------------------- Mobile menu ----------------------- */
        const navToggle = document.querySelector(".nav-toggle");
        const navLinks = document.querySelector(".nav-links");
        if (navToggle && navLinks) {
            navToggle.addEventListener("click", function () {
                const open = navLinks.classList.toggle("is-open");
                navToggle.setAttribute("aria-expanded", String(open));
            });
            navLinks.querySelectorAll("a").forEach((a) =>
                a.addEventListener("click", () => navLinks.classList.remove("is-open"))
            );
        }

        /* ----------------------- Navbar dropdowns (mobile tap) ----------------------- */
        document.querySelectorAll(".has-dropdown > .dropdown-toggle").forEach((toggle) => {
            toggle.addEventListener("click", function (e) {
                if (window.innerWidth <= 992) {
                    e.preventDefault();
                    this.parentElement.classList.toggle("is-open");
                }
            });
        });

        /* ----------------------- Scroll progress bar ----------------------- */
        const progress = document.querySelector(".scroll-progress");
        if (progress) {
            const updateProgress = () => {
                const h = document.documentElement;
                const scrolled = (h.scrollTop) / (h.scrollHeight - h.clientHeight);
                progress.style.width = (scrolled * 100).toFixed(2) + "%";
            };
            updateProgress();
            window.addEventListener("scroll", updateProgress, { passive: true });
        }

        /* ----------------------- Back to top ----------------------- */
        const toTop = document.querySelector(".back-to-top");
        if (toTop) {
            window.addEventListener("scroll", () => {
                toTop.classList.toggle("is-visible", window.scrollY > 600);
            }, { passive: true });
            toTop.addEventListener("click", () => window.scrollTo({ top: 0, behavior: "smooth" }));
        }

        /* ----------------------- AOS init (if loaded) ----------------------- */
        if (window.AOS) {
            window.AOS.init({ duration: 800, once: true, offset: 60, easing: "ease-out-cubic" });
        }

        /* ----------------------- Reveal fallback (when AOS absent) ----------------------- */
        const reveals = document.querySelectorAll("[data-reveal]");
        if (reveals.length && "IntersectionObserver" in window) {
            const io = new IntersectionObserver((entries) => {
                entries.forEach((e) => {
                    if (e.isIntersecting) { e.target.classList.add("is-revealed"); io.unobserve(e.target); }
                });
            }, { threshold: 0.15 });
            reveals.forEach((el) => io.observe(el));
        }

        /* ----------------------- Animated counters ----------------------- */
        const counters = document.querySelectorAll("[data-counter]");
        if (counters.length && "IntersectionObserver" in window) {
            const animate = (el) => {
                const target = parseFloat(el.getAttribute("data-counter")) || 0;
                const dur = 1600;
                const start = performance.now();
                const suffix = el.getAttribute("data-suffix") || "";
                const step = (now) => {
                    const p = Math.min((now - start) / dur, 1);
                    const eased = 1 - Math.pow(1 - p, 3);
                    el.textContent = Math.floor(eased * target).toLocaleString() + suffix;
                    if (p < 1) requestAnimationFrame(step);
                    else el.textContent = target.toLocaleString() + suffix;
                };
                requestAnimationFrame(step);
            };
            const io = new IntersectionObserver((entries) => {
                entries.forEach((e) => {
                    if (e.isIntersecting) { animate(e.target); io.unobserve(e.target); }
                });
            }, { threshold: 0.4 });
            counters.forEach((el) => io.observe(el));
        }

        /* ----------------------- GSAP hero (if loaded) ----------------------- */
        if (window.gsap) {
            const heroItems = document.querySelectorAll("[data-gsap-hero]");
            if (heroItems.length) {
                window.gsap.from(heroItems, {
                    y: 40, opacity: 0, duration: 1, ease: "power3.out", stagger: 0.15, delay: 0.3
                });
            }
        }

        /* ----------------------- Swiper (if loaded) ----------------------- */
        if (window.Swiper) {
            document.querySelectorAll("[data-swiper='hero']").forEach((el) => {
                new window.Swiper(el, {
                    loop: true, effect: "fade", speed: 1000,
                    autoplay: { delay: 6000, disableOnInteraction: false },
                    fadeEffect: { crossFade: true },
                    pagination: { el: el.querySelector(".swiper-pagination"), clickable: true },
                });
            });
            document.querySelectorAll("[data-swiper='cards']").forEach((el) => {
                new window.Swiper(el, {
                    slidesPerView: 1.1, spaceBetween: 20, grabCursor: true,
                    pagination: { el: el.querySelector(".swiper-pagination"), clickable: true },
                    breakpoints: { 640: { slidesPerView: 2 }, 992: { slidesPerView: 3 } },
                });
            });
        }

        /* ----------------------- Gallery filters ----------------------- */
        const filterBar = document.querySelector(".gallery-filters");
        if (filterBar) {
            filterBar.addEventListener("click", (e) => {
                const btn = e.target.closest("button[data-filter]");
                if (!btn) return;
                filterBar.querySelectorAll("button").forEach((b) => b.classList.remove("active"));
                btn.classList.add("active");
                const f = btn.getAttribute("data-filter");
                document.querySelectorAll(".gallery-item").forEach((item) => {
                    const cat = item.getAttribute("data-category");
                    item.style.display = (f === "all" || f === cat) ? "" : "none";
                });
            });
        }

        /* ----------------------- Password visibility toggle ----------------------- */
        document.querySelectorAll("[data-toggle-password]").forEach((btn) => {
            btn.addEventListener("click", function () {
                const input = document.querySelector(this.getAttribute("data-toggle-password"));
                if (!input) return;
                const show = input.type === "password";
                input.type = show ? "text" : "password";
                this.innerHTML = show ? '<i class="bi bi-eye-slash"></i>' : '<i class="bi bi-eye"></i>';
            });
        });

        /* ----------------------- OTP inputs UX ----------------------- */
        const otpWrap = document.querySelector(".otp-inputs");
        if (otpWrap) {
            const inputs = Array.from(otpWrap.querySelectorAll("input"));
            const hidden = document.querySelector("[data-otp-value]");
            const sync = () => { if (hidden) hidden.value = inputs.map((i) => i.value).join(""); };
            inputs.forEach((input, idx) => {
                input.addEventListener("input", () => {
                    input.value = input.value.replace(/\D/g, "").slice(0, 1);
                    if (input.value && idx < inputs.length - 1) inputs[idx + 1].focus();
                    sync();
                });
                input.addEventListener("keydown", (e) => {
                    if (e.key === "Backspace" && !input.value && idx > 0) inputs[idx - 1].focus();
                });
                input.addEventListener("paste", (e) => {
                    e.preventDefault();
                    const data = (e.clipboardData.getData("text") || "").replace(/\D/g, "").slice(0, inputs.length);
                    data.split("").forEach((ch, i) => { if (inputs[i]) inputs[i].value = ch; });
                    sync();
                    (inputs[data.length] || inputs[inputs.length - 1]).focus();
                });
            });
        }

        /* ----------------------- Resend OTP countdown ----------------------- */
        const resendBtn = document.querySelector("[data-resend]");
        const resendTimer = document.querySelector("[data-resend-timer]");
        if (resendBtn && resendTimer) {
            let remaining = parseInt(resendBtn.getAttribute("data-resend") || "60", 10);
            const tick = () => {
                if (remaining <= 0) {
                    resendBtn.disabled = false;
                    resendTimer.textContent = "";
                    return;
                }
                resendBtn.disabled = true;
                resendTimer.innerHTML = "Resend available in <strong>" + remaining + "s</strong>";
                remaining -= 1;
                setTimeout(tick, 1000);
            };
            tick();
        }

        /* ----------------------- Dashboard sidebar (mobile) ----------------------- */
        const menuBtn = document.querySelector(".dash__menu-btn");
        const sidebar = document.querySelector(".dash__sidebar");
        const overlay = document.querySelector(".dash__overlay");
        if (menuBtn && sidebar) {
            const close = () => { sidebar.classList.remove("is-open"); overlay && overlay.classList.remove("is-open"); };
            menuBtn.addEventListener("click", () => {
                sidebar.classList.toggle("is-open");
                overlay && overlay.classList.toggle("is-open");
            });
            overlay && overlay.addEventListener("click", close);
        }

        /* Persist sidebar scroll position so navigating between dashboard pages
           doesn't reset it back to the top. */
        if (sidebar) {
            const KEY = "seu-sidebar-scroll";
            const saved = sessionStorage.getItem(KEY);
            if (saved !== null) {
                sidebar.scrollTop = parseInt(saved, 10) || 0;
            }
            let raf = null;
            sidebar.addEventListener("scroll", () => {
                if (raf) return;
                raf = requestAnimationFrame(() => {
                    sessionStorage.setItem(KEY, String(sidebar.scrollTop));
                    raf = null;
                });
            }, { passive: true });
        }
    });
})();



/* ==========================================================================
   Component layer: Toasts, Confirm modal, Image upload, Page transitions,
   and the progressive Data-Table engine. Self-contained IIFE.
   ========================================================================== */
(function () {
    "use strict";

    /* ----------------------- Toast notifications ----------------------- */
    const ICONS = {
        success: "bi-check-circle-fill",
        error: "bi-x-circle-fill",
        info: "bi-info-circle-fill",
        warning: "bi-exclamation-triangle-fill"
    };
    function ensureStack() {
        let stack = document.querySelector(".toast-stack");
        if (!stack) {
            stack = document.createElement("div");
            stack.className = "toast-stack";
            stack.setAttribute("aria-live", "polite");
            stack.setAttribute("aria-atomic", "true");
            document.body.appendChild(stack);
        }
        return stack;
    }
    window.Toast = {
        show: function (opts) {
            opts = opts || {};
            const type = opts.type || "info";
            const stack = ensureStack();
            const el = document.createElement("div");
            el.className = "toast toast--" + type;
            el.setAttribute("role", type === "error" ? "alert" : "status");
            el.innerHTML =
                '<i class="toast__icon bi ' + (ICONS[type] || ICONS.info) + '"></i>' +
                '<div class="toast__body">' +
                (opts.title ? '<div class="toast__title"></div>' : '') +
                '<div class="toast__msg"></div></div>' +
                '<button class="toast__close" type="button" aria-label="Dismiss"><i class="bi bi-x-lg"></i></button>';
            if (opts.title) el.querySelector(".toast__title").textContent = opts.title;
            el.querySelector(".toast__msg").textContent = opts.message || "";
            stack.appendChild(el);
            requestAnimationFrame(() => el.classList.add("is-shown"));
            const remove = () => {
                el.classList.remove("is-shown");
                setTimeout(() => el.remove(), 400);
            };
            el.querySelector(".toast__close").addEventListener("click", remove);
            if (opts.duration !== 0) setTimeout(remove, opts.duration || 4500);
        },
        success: function (m, t) { this.show({ type: "success", message: m, title: t }); },
        error: function (m, t) { this.show({ type: "error", message: m, title: t }); },
        info: function (m, t) { this.show({ type: "info", message: m, title: t }); },
        warning: function (m, t) { this.show({ type: "warning", message: m, title: t }); }
    };

    /* ----------------------- Confirm modal ----------------------- */
    // Markup provided by fragments/ui :: overlays (#confirmModal). Trigger via
    // data-confirm="message" on a link, button or form-submit button.
    function initConfirmModal() {
        const modal = document.getElementById("confirmModal");
        if (!modal) return;
        const titleEl = modal.querySelector("[data-confirm-title]");
        const msgEl = modal.querySelector("[data-confirm-message]");
        const okBtn = modal.querySelector("[data-confirm-ok]");
        let confirmCb = null;

        const close = () => { modal.classList.remove("is-open"); confirmCb = null; };
        const open = (opts) => {
            if (titleEl) titleEl.textContent = opts.title || "Are you sure?";
            if (msgEl) msgEl.textContent = opts.message || "This action cannot be undone.";
            okBtn.textContent = opts.okText || "Delete";
            confirmCb = opts.onConfirm || null;
            modal.classList.add("is-open");
        };
        // Public API for programmatic confirmation (used by bulk actions).
        window.SeuConfirm = open;

        modal.addEventListener("click", (e) => {
            if (e.target === modal || e.target.closest("[data-confirm-cancel]")) close();
        });
        document.addEventListener("keydown", (e) => { if (e.key === "Escape") close(); });
        okBtn.addEventListener("click", () => { const cb = confirmCb; close(); if (cb) cb(); });

        // Declarative confirmation on links / form-submit buttons.
        document.addEventListener("click", (e) => {
            const trigger = e.target.closest("[data-confirm]");
            if (!trigger) return;
            e.preventDefault();
            const form = trigger.closest("form");
            const href = trigger.tagName === "A" ? trigger.getAttribute("href") : null;
            open({
                title: trigger.getAttribute("data-confirm-title"),
                message: trigger.getAttribute("data-confirm"),
                okText: trigger.getAttribute("data-confirm-ok-text"),
                onConfirm: () => { if (form) form.submit(); else if (href) window.location.href = href; }
            });
        });
    }

    function metaContent(name) {
        const el = document.querySelector('meta[name="' + name + '"]');
        return el ? el.getAttribute("content") : "";
    }

    /* ----------------------- Image upload (preview + drag & drop) ----------------------- */
    function initDropzones() {
        document.querySelectorAll("[data-dropzone]").forEach((zone) => {
            const input = zone.querySelector('input[type="file"]');
            const previews = zone.querySelector("[data-dz-previews]");
            if (!input || !previews) return;

            const render = (files) => {
                previews.innerHTML = "";
                Array.from(files).forEach((file) => {
                    if (!file.type.startsWith("image/")) return;
                    const url = URL.createObjectURL(file);
                    const cell = document.createElement("div");
                    cell.className = "dropzone__preview";
                    cell.innerHTML = '<img alt="Preview"><button type="button" aria-label="Remove"><i class="bi bi-x"></i></button>';
                    cell.querySelector("img").src = url;
                    previews.appendChild(cell);
                });
            };
            zone.addEventListener("click", (e) => { if (!e.target.closest(".dropzone__preview")) input.click(); });
            input.addEventListener("change", () => render(input.files));
            ["dragenter", "dragover"].forEach((ev) =>
                zone.addEventListener(ev, (e) => { e.preventDefault(); zone.classList.add("is-dragover"); }));
            ["dragleave", "drop"].forEach((ev) =>
                zone.addEventListener(ev, (e) => { e.preventDefault(); zone.classList.remove("is-dragover"); }));
            zone.addEventListener("drop", (e) => {
                if (e.dataTransfer && e.dataTransfer.files.length) {
                    input.files = e.dataTransfer.files;
                    render(input.files);
                }
            });
        });
    }

    /* ----------------------- Page transitions ----------------------- */
    function initPageTransitions() {
        const reduce = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
        if (reduce) return;
        document.body.classList.add("page-fade");
        document.addEventListener("click", (e) => {
            const a = e.target.closest("a");
            if (!a) return;
            const href = a.getAttribute("href");
            if (!href || a.target === "_blank" || a.hasAttribute("download") ||
                a.dataset.noTransition !== undefined || href.startsWith("#") ||
                href.startsWith("mailto:") || href.startsWith("tel:") ||
                a.origin !== window.location.origin || e.metaKey || e.ctrlKey || e.shiftKey) return;
            e.preventDefault();
            document.body.classList.add("is-leaving");
            setTimeout(() => { window.location.href = href; }, 220);
        });
        window.addEventListener("pageshow", (e) => { if (e.persisted) document.body.classList.remove("is-leaving"); });
    }

    /* ----------------------- Data table engine ----------------------- */
    function csvEscape(v) { return '"' + String(v).replace(/"/g, '""') + '"'; }

    function initDataTable(root) {
        const table = root.querySelector("table");
        if (!table) return;
        const tbody = table.querySelector("tbody");
        let rows = Array.from(tbody.querySelectorAll("tr"));
        const pageSize = parseInt(root.getAttribute("data-page-size") || "10", 10);
        let currentPage = 1, sortCol = -1, sortDir = 1, query = "", filterCol = -1, filterVal = "";

        const search = root.querySelector("[data-dt-search]");
        const filter = root.querySelector("[data-dt-filter]");
        const pagesEl = root.querySelector("[data-dt-pages]");
        const infoEl = root.querySelector("[data-dt-info]");
        const selectAll = root.querySelector("[data-dt-select-all]");
        const bulkbar = root.querySelector("[data-dt-bulkbar]");
        const countEl = root.querySelector("[data-dt-count]");
        if (filter) filterCol = parseInt(filter.getAttribute("data-col") || "-1", 10);

        function filtered() {
            return rows.filter((r) => {
                const text = r.textContent.toLowerCase();
                const okQuery = !query || text.indexOf(query) !== -1;
                let okFilter = true;
                if (filterVal && filterCol >= 0) {
                    const cell = r.children[filterCol];
                    okFilter = cell && cell.textContent.trim().toLowerCase() === filterVal.toLowerCase();
                }
                return okQuery && okFilter;
            });
        }
        function render() {
            let data = filtered();
            if (sortCol >= 0) {
                data = data.slice().sort((a, b) => {
                    const x = a.children[sortCol] ? a.children[sortCol].textContent.trim() : "";
                    const y = b.children[sortCol] ? b.children[sortCol].textContent.trim() : "";
                    const nx = parseFloat(x.replace(/[^0-9.-]/g, "")), ny = parseFloat(y.replace(/[^0-9.-]/g, ""));
                    if (!isNaN(nx) && !isNaN(ny)) return (nx - ny) * sortDir;
                    return x.localeCompare(y) * sortDir;
                });
            }
            const total = data.length;
            const totalPages = Math.max(1, Math.ceil(total / pageSize));
            if (currentPage > totalPages) currentPage = totalPages;
            const start = (currentPage - 1) * pageSize;
            rows.forEach((r) => (r.style.display = "none"));
            data.slice(start, start + pageSize).forEach((r) => (r.style.display = ""));

            if (infoEl) infoEl.textContent = total ? ("Showing " + (start + 1) + "-" + Math.min(start + pageSize, total) + " of " + total) : "No matching records";
            if (pagesEl) {
                pagesEl.innerHTML = "";
                const mkBtn = (label, page, disabled, active) => {
                    const b = document.createElement("button");
                    b.type = "button"; b.textContent = label;
                    if (disabled) b.disabled = true; if (active) b.classList.add("active");
                    b.addEventListener("click", () => { currentPage = page; render(); });
                    pagesEl.appendChild(b);
                };
                mkBtn("‹", currentPage - 1, currentPage === 1, false);
                for (let p = 1; p <= totalPages; p++) mkBtn(String(p), p, false, p === currentPage);
                mkBtn("›", currentPage + 1, currentPage === totalPages, false);
            }
            updateBulk();
        }
        function updateBulk() {
            const checks = rows.filter((r) => r.style.display !== "none").map((r) => r.querySelector("[data-dt-row]")).filter(Boolean);
            const selected = checks.filter((c) => c.checked).length;
            if (bulkbar) bulkbar.classList.toggle("is-active", selected > 0);
            if (countEl) countEl.textContent = selected;
            if (selectAll) selectAll.checked = checks.length > 0 && selected === checks.length;
        }

        if (search) search.addEventListener("input", () => { query = search.value.trim().toLowerCase(); currentPage = 1; render(); });
        if (filter) filter.addEventListener("change", () => { filterVal = filter.value; currentPage = 1; render(); });
        table.querySelectorAll("th[data-sort]").forEach((th, i) => {
            const colIndex = Array.from(th.parentElement.children).indexOf(th);
            th.addEventListener("click", () => {
                if (sortCol === colIndex) sortDir *= -1; else { sortCol = colIndex; sortDir = 1; }
                table.querySelectorAll("th[data-sort]").forEach((h) => h.classList.remove("sort-asc", "sort-desc"));
                th.classList.add(sortDir === 1 ? "sort-asc" : "sort-desc");
                render();
            });
        });
        if (selectAll) selectAll.addEventListener("change", () => {
            rows.filter((r) => r.style.display !== "none").forEach((r) => {
                const c = r.querySelector("[data-dt-row]"); if (c) c.checked = selectAll.checked;
            });
            updateBulk();
        });
        tbody.addEventListener("change", (e) => { if (e.target.matches("[data-dt-row]")) updateBulk(); });

        const bulkDelete = root.querySelector("[data-dt-bulk-delete]");
        if (bulkDelete) bulkDelete.addEventListener("click", () => {
            const ids = rows.filter((r) => { const c = r.querySelector("[data-dt-row]"); return c && c.checked; })
                .map((r) => r.querySelector("[data-dt-row]").value);
            if (!ids.length) return;
            const action = root.getAttribute("data-bulk-action");
            const doDelete = () => {
                if (!action) {
                    document.dispatchEvent(new CustomEvent("dt:bulk-delete", { detail: { ids: ids, table: root.id } }));
                    return;
                }
                const form = document.createElement("form");
                form.method = "POST";
                form.action = action;
                ids.forEach((id) => {
                    const i = document.createElement("input");
                    i.type = "hidden"; i.name = "ids"; i.value = id;
                    form.appendChild(i);
                });
                const param = metaContent("_csrf_param");
                const token = metaContent("_csrf");
                if (param && token) {
                    const c = document.createElement("input");
                    c.type = "hidden"; c.name = param; c.value = token;
                    form.appendChild(c);
                }
                document.body.appendChild(form);
                form.submit();
            };
            if (window.SeuConfirm) {
                window.SeuConfirm({
                    title: "Delete selected?",
                    message: "This will permanently delete " + ids.length + " item(s). This cannot be undone.",
                    okText: "Delete all",
                    onConfirm: doDelete
                });
            } else {
                doDelete();
            }
        });

        const exportBtn = root.querySelector("[data-dt-export]");
        if (exportBtn) exportBtn.addEventListener("click", () => exportData("csv"));

        const exportExcelBtn = root.querySelector("[data-dt-export-excel]");
        if (exportExcelBtn) exportExcelBtn.addEventListener("click", () => exportData("excel"));

        function exportData(format) {
            const headers = Array.from(table.querySelectorAll("thead th"))
                .filter((th) => !th.hasAttribute("data-dt-skip"))
                .map((th) => th.textContent.trim());
            const rowData = filtered().map((r) =>
                Array.from(r.children)
                    .filter((td) => !td.hasAttribute("data-dt-skip"))
                    .map((td) => td.textContent.trim()));
            const name = root.getAttribute("data-export-name") || "export";

            if (format === "excel") {
                let html = "<table><thead><tr>" +
                    headers.map((h) => "<th>" + escapeHtml(h) + "</th>").join("") + "</tr></thead><tbody>";
                rowData.forEach((row) => {
                    html += "<tr>" + row.map((c) => "<td>" + escapeHtml(c) + "</td>").join("") + "</tr>";
                });
                html += "</tbody></table>";
                const blob = new Blob(
                    ['<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel">' +
                     '<head><meta charset="utf-8"></head><body>' + html + "</body></html>"],
                    { type: "application/vnd.ms-excel" });
                downloadBlob(blob, name + ".xls");
            } else {
                const lines = [headers.map(csvEscape).join(",")];
                rowData.forEach((row) => lines.push(row.map(csvEscape).join(",")));
                const blob = new Blob(["\uFEFF" + lines.join("\n")], { type: "text/csv;charset=utf-8;" });
                downloadBlob(blob, name + ".csv");
            }
        }

        function escapeHtml(s) {
            return String(s).replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
        }
        function downloadBlob(blob, filename) {
            const a = document.createElement("a");
            a.href = URL.createObjectURL(blob);
            a.download = filename;
            a.click();
        }

        render();
    }

    document.addEventListener("DOMContentLoaded", function () {
        initConfirmModal();
        initDropzones();
        initPageTransitions();
        document.querySelectorAll("[data-enhanced-table]").forEach(initDataTable);

        // Reload buttons (avoid javascript: hrefs for CSP friendliness)
        document.querySelectorAll("[data-reload]").forEach((b) =>
            b.addEventListener("click", (e) => { e.preventDefault(); window.location.reload(); }));

        // Auto-show server flash toasts: <div class="js-flash" data-type="success" data-message="..."></div>
        document.querySelectorAll(".js-flash").forEach((el) => {
            window.Toast.show({ type: el.getAttribute("data-type") || "info", message: el.getAttribute("data-message") || "" });
        });
    });
})();
