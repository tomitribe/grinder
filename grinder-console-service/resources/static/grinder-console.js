window.onload = function() {
    function addChangeDetection() {
        var labels = document.getElementsByTagName("label");
        for ( var i = 0; i < labels.length; ++i) {
            var l = labels[i];

            if (l.htmlFor != '') {
                var e = document.getElementById(l.htmlFor);

                if (e) {
                    e.label = l;
                } else {
                    var labeleds = document.getElementsByName(l.htmlFor);

                    for ( var j = 0; j < labeleds.length; ++j) {
                        labeleds[j].label = l;
                    }
                }
            }
        }

        var saveButton = document.getElementById("submit");
        saveButton.classList.add("hidden");
        var changeables = document.getElementsByClassName("changeable");

        for ( var i = 0; i < changeables.length; ++i) {
            var c = changeables[i];

            if (c.type === "checkbox") {
                c.modified = function() {
                    return this.checked != this.defaultChecked;
                };
            } else {
                c.original = c.value;
                c.modified = function() {
                    return this.original != this.value;
                };
            }

            c.addEventListener("change", function(e) {
                if (e.target.modified()) {
                    e.target.label.classList.add("changed");
                } else {
                    e.target.label.classList.remove("changed");
                }

                if (Array.prototype.some.call(changeables, function(x) {
                    return x.modified();
                })) {
                    saveButton.classList.remove("hidden");
                } else {
                    saveButton.classList.add("hidden");
                }
            });
        }
    }

    addChangeDetection();
};
