
function filter(tableName, colSeq, filterName) {
	// Declare variables and initialize
	var input, filter, table, tr, td, i;
	input = document.getElementById(filterName);
	filter = input.value.toUpperCase();
	table = document.getElementById(tableName);
	tr = table.getElementsByTagName("tr");

	// Loop through all table rows, and hide those not matching the filtering pattern.
	for (i = 0; i < tr.length; i++) {
		td = tr[i].getElementsByTagName("td")[colSeq];
		if (td) {
			if (td.innerText.toUpperCase().indexOf(filter) > -1) {
				tr[i].style.display = "";
			} else {
				tr[i].style.display = "none";
			}
		}
	}	
}

