console.log("this is script")

const toggleSidebar= () =>{

    if($('.sidebar').is(":visible")){

         $(".sidebar").css("display","none");
         $(".content").css("margin-left","0%")
    }else{
        $(".sidebar").css("display","block");
        $(".content").css("margin-left","20%")
    }


};

/* ===== IMAGE PREVIEW ===== */
function previewImage(event) {
    const reader = new FileReader();
    reader.onload = function () {
        document.getElementById('imgPreview').src = reader.result;
    }
    reader.readAsDataURL(event.target.files[0]);
}

/* ===== THEME TOGGLE ===== */
function toggleTheme() {
    document.body.classList.toggle("dark");
}

/* ===== AUTO-HIDE ALERTS (moved from templates) ===== */
setTimeout(function () {
    if (window.jQuery) {
        $('.alert').fadeOut('slow');
    }
}, 3000);

/* ===== CATEGORY / GLOBAL FUNCTIONS (moved from templates) ===== */
function toggleCategoryBox() {
    const box = document.getElementById("newCategoryBox");
    if (box) {
        box.style.display = box.style.display === "none" ? "block" : "none";
    }
}

function saveCategory() {
    const titleEl = document.getElementById("newCategoryTitle");
    const descEl = document.getElementById("newCategoryDesc");
    if (!titleEl || !descEl) return;

    const title = titleEl.value;
    const description = descEl.value;

    if (!title || !description) {
        if (window.Swal) {
            Swal.fire({
                title: 'Oops!',
                text: 'Please fill all fields',
                icon: 'warning'
            });
        }
        return;
    }

    fetch("/category/save", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: "title=" + encodeURIComponent(title) + "&description=" + encodeURIComponent(description)
    })
        .then(response => response.json())
        .then(data => {
            const select = document.getElementById("categorySelect");
            if (select) {
                const option = document.createElement("option");
                option.value = data.categoryId;
                option.text = data.categoryTitle;
                select.add(option);
                select.value = data.categoryId;
            }

            if (window.Swal) {
                Swal.fire({
                    title: '🎉 Category Added!',
                    text: data.categoryTitle + ' created successfully',
                    icon: 'success',
                    timer: 2000,
                    showConfirmButton: false
                });
            }

            titleEl.value = "";
            descEl.value = "";
            if (document.getElementById("newCategoryBox")) {
                document.getElementById("newCategoryBox").style.display = "none";
            }
        })
        .catch(error => {
            console.error(error);
            if (window.Swal) {
                Swal.fire({
                    title: 'Error!',
                    text: 'Something went wrong',
                    icon: 'error'
                });
            }
        });
}

function deleteContact(cid) {
    if (!window.Swal) {
        if (confirm('Are you sure? This action cannot be undone!')) {
            window.location = "/user/delete/" + cid;
        }
        return;
    }

    Swal.fire({
        title: "Are you sure?",
        text: "This action cannot be undone!",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#d33",
        confirmButtonText: "Yes, delete it!"
    }).then((result) => {
        if (result.isConfirmed) {
            window.location = "/user/delete/" + cid;
        }
    });
}

/* ===== LIVE SEARCH TYPEAHEAD ===== */
function debounce(fn, delay) {
    let t;
    return function (...args) {
        clearTimeout(t);
        t = setTimeout(() => fn.apply(this, args), delay);
    };
}

function initContactTypeahead() {
    const input = document.getElementById('searchInput');
    const box = document.getElementById('suggestions');
    if (!input || !box) return;

    const render = (items) => {
        box.innerHTML = '';
        if (!items || items.length === 0) {
            box.style.display = 'none';
            return;
        }

        items.forEach(item => {
            const a = document.createElement('a');
            a.className = 'list-group-item list-group-item-action bg-dark text-white';
            a.style.cursor = 'pointer';
            a.innerHTML = `<strong>${escapeHtml(item.name)}</strong><br/><small class="text-muted">${escapeHtml(item.email || '')} • ${escapeHtml(item.phone || '')}</small>`;
            a.addEventListener('click', () => {
                // Navigate to contact details on click
                window.location.href = '/user/' + item.id + '/contact';
            });
            box.appendChild(a);
        });

        box.style.display = 'block';
    };

    const fetchSuggestions = debounce(function () {
        const q = input.value.trim();
        if (q.length === 0) {
            render([]);
            return;
        }

        fetch('/user/search_suggest?query=' + encodeURIComponent(q), {credentials: 'same-origin'})
            .then(r => r.json())
            .then(data => render(data))
            .catch(err => {
                console.error('Suggestion fetch failed', err);
                render([]);
            });
    }, 250);

    input.addEventListener('input', fetchSuggestions);

    // close suggestions when clicking outside
    document.addEventListener('click', (e) => {
        if (!box.contains(e.target) && e.target !== input) {
            box.style.display = 'none';
        }
    });
}

function escapeHtml(str) {
    if (!str) return '';
    return String(str).replace(/[&<>"'`]/g, function (s) {
        return ({
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#39;',
            '`': '&#96;'
        })[s];
    });
}

// Initialize typeahead on DOM ready
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initContactTypeahead);
} else {
    initContactTypeahead();
}
