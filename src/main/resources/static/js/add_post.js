document.addEventListener("DOMContentLoaded", function() {

	tinymce.init({
		selector: '#editor',
		height: 300,
		menubar: false,
		plugins: 'lists link image code',
		toolbar: 'undo redo | bold italic | alignleft aligncenter alignright | bullist numlist | code',
		skin: 'oxide-dark',
		content_css: 'dark'
	});

});

// Initialize TinyMCE for update form textarea if present
document.addEventListener("DOMContentLoaded", function() {
	if (window.tinymce && document.querySelector('#mytextarea')) {
		tinymce.init({
			selector: '#mytextarea',
			height: 120
		});
	}
});
