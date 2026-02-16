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
