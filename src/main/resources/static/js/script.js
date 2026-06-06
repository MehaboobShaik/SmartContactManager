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
