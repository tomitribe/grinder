window.onload = function() {
	var labels = document.getElementsByTagName("label");
	for (var i = 0; i < labels.length; i++) {
	    if (labels[i].htmlFor != '') {
	         var elem = document.getElementById(labels[i].htmlFor);
	         if (elem) {
	            elem.label = labels[i];
	         }
	    }
	}

	var saveButton = document.getElementById("submit")
	saveButton.classList.add("hidden")
	var changeables = document.getElementsByClassName("changeable");

	for (var i = 0; i < changeables.length; ++i) {
		changeables[i].addEventListener(
			"change",
			function(e) {
				saveButton.classList.remove("hidden")
				e.target.label.classList.add("changed");
			});
	}
}
