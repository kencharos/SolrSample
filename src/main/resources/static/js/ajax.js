function ajaxGet(url, param, successFunc, errorFunc) {
	$.ajax({url:url, 
        method:"GET", 
        data:param }
	).success(function(data){
		successFunc ? successFunc(data) : alert(data.message);
	}).error( function(data){
		errorFunc ? errorFunc(data) : alert(data.message);
	});
}