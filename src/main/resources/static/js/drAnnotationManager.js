
const ANNOTATIONTYPE = {
	HIGHLIGHT		: "HIGHLIGHT",
	STRIKETHROUGH	: "STRIKETHROUGH",
	TAG				: "TAG"
}

const endIndex   = document.URL.lastIndexOf("/");
const beginIndex = document.URL.lastIndexOf("/", endIndex -1);
const documentId = document.URL.substring(beginIndex + 1, endIndex);


let token;
async function getCsrfToken() {
	token = await fetch('/rest/annotations/csrfToken', {
		method: 'GET'
	}).then(function(response){
		return response.text();
	}).then(function(data){
		return data;
	});
} 
getCsrfToken();


	
//add stylesheets of deepreadings...
//they must be placed before other styling elements so that they do not
//gain precedence over the document's original styling
document.head.insertAdjacentHTML("afterbegin", 
		`<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.15.4/css/all.css" crossorigin="anonymous"/>
		<link rel="stylesheet" href="https://www.w3schools.com/lib/w3.css"/>
		<link rel="stylesheet" href="/css/drStyle.css" type="text/css"/>
`);



//add NAVIGATION MENU...
document.body.insertAdjacentHTML("afterbegin",
	`<nav class="w3-top w3-bar w3-dark-grey drNavBar" >
		<a class="w3-bar-item w3-button" href="/documents"> Books</a>	
		<div class="w3-dropdown-hover w3-right">
     		<a href="?lang=en" class="w3-bar-item w3-button">Other Deep Readers</a>
		</div>
	</nav>`
);




//add CONTEXTMENU
document.body.insertAdjacentHTML("afterbegin", 
		`<div id="drContextMenu" style="display: none;" class="w3-bar-block">
			<div id="drMenuItemHighlight" class="w3-bar-item w3-text-teal w3-hover-red" ><i class="fa fa-highlighter"></i> Highlight </div>
			<div id="drMenuItemStrikethrough" class="w3-bar-item w3-text-teal w3-hover-red" ><i class="fa fa-strikethrough"></i> Strikethrough </div>
			<div id="drMenuItemAddTag" class="w3-bar-item w3-text-teal w3-hover-red" ><i class="fa fa-tag"></i> Tag </div>
			<div id="drMenuItemDeleteAnnotation" class="w3-bar-item w3-text-red w3-hover-red" ><i class="fa fa-times"></i> Delete </div>
			<div id="drMenuItemShowAllAnnotations" class="w3-bar-item w3-hover-red"><i class="fas fa-eye"></i> Show/Hide all markers</div>
		</div>`
);
let contextMenu = document.getElementById("drContextMenu");



//add ANNOTATIONDIALOG
document.body.insertAdjacentHTML("afterbegin",
`  <div id="drAnnotationDialog" class="w3-modal" style="display: none;">
      <div id="drAnnotationDialogContent" class="w3-modal-content w3-card-4 w3-animate-zoom" style="position:absolute; width:fit-content; cursor: move">
         <header class="w3-container w3-black w3-margin-bottom">
            <span id="drAnnotationCancel" onclick="document.getElementById('drAnnotationDialog').style.display='none'" class="w3-button w3-hover-red w3-xlarge w3-display-topright">&times;</span>
            <h4 style="margin: 0px;">Marker Details</h4>
            <p id="drAnnotationInfo" style="margin: 0px; text-align:center"></p>
         </header>

  		
   		<div class="w3-row">
   			<div class="w3-container w3-quarter">
          		<p><strong>Type: </strong></p>
 			</div>
   			<div class="w3-rest">
           		<input type="radio" id="drHighlightRadio" name="drAnnotationType" value="HIGHLIGHT"
    			onchange="document.getElementById('drAnnotationComment').style.display='block'; document.getElementById('drAnnotationTags').style.display='none';">
 		      	<label for="drHighlightRadio" title="Use Highlight to express approval, importance or any kind of positive opinion.">Hightlight</label><br> 

   				<input type="radio" id="drStrikethroughRadio" name="drAnnotationType" value="STRIKETHROUGH" 
   				onchange="document.getElementById('drAnnotationComment').style.display='block'; document.getElementById('drAnnotationTags').style.display='none';">
  				<label for="drStrikethroughRadio" title="Use Strikethrough to express disapproval.">Strikethrough</label><br> 

   				<input type="radio" id="drTagRadio" name="drAnnotationType" value="TAG"
   				onchange="document.getElementById('drAnnotationComment').style.display='none'; document.getElementById('drAnnotationTags').style.display='block';">
  				<label for="drTagRadio" title="Use appropriate tags to facilitate the future searches about those tags.">Tag</label><br> 
  			 </div>       
		</div>
        <div class="w3-container w3-padding">
            <textarea id="drAnnotationComment" class="w3-input w3-border" style="height:300px;" placeholder="Optionally you may enter comments about the marked text..." ></textarea>
            <textarea id="drAnnotationTags" class="w3-input w3-border" style="height:300px;" placeholder="Enter tags for the marked text. Separate the tags with commas. Multi-word tags allowed ie. 'greenhouse effect', 'climate change', etc." ></textarea>
        </div>
         <div class="w3-container">
            <button id="drAnnotationOk" class="ok w3-button w3-block w3-dark-grey w3-hover-red w3-section w3-padding">Save</button>
         </div>
		

     </div>
  </div>`
);

let annotationsMap = new Map();
let annotationsDiv = document.createElement("div");
annotationsDiv.setAttribute("id", "drAnnotations");
document.body.prepend(annotationsDiv);





document.addEventListener("contextmenu", function(e){
	e.preventDefault();
	contextMenu.style.display = "block";
	contextMenu.style.left = (e.pageX - 10) + "px";
	contextMenu.style.top  = (e.pageY - 10) + "px";  
	
	let elementsFromPoint = document.elementsFromPoint(e.clientX, e.clientY);
	let contextAnnotation = elementsFromPoint.find(element => element.hasAttribute("data-annotation-id"));
	if (contextAnnotation) {
		contextMenu.setAttribute("data-context-annotation-id", contextAnnotation.getAttribute("data-annotation-id"));
	}
}, false);



document.onmousedown = function(e) {
	if (contextMenu.contains(e.target)) {
		e.preventDefault();//so as not to collapse the selection.
	} else {
		contextMenu.style.display="none";
	}	
};



document.onclick = function(e) {
	const elementsFromPoint = document.elementsFromPoint(e.clientX, e.clientY);	
	const clickedAnnotationElm = elementsFromPoint.find(element => element.hasAttribute("data-annotation-id"));
	console.log(clickedAnnotationElm);
	function eqArray(a1, a2) {
		console.log(a1.length);
		console.log(a2.length);
    	if (a1.length !== a2.length) return false;
    	for (const element of a1) {
			if (!a2.includes(element)) return false;
		}
    	return true;
	}
	if (clickedAnnotationElm) {
		const clickedAnnotationId = Number(clickedAnnotationElm.getAttribute("data-annotation-id"));
		const clickedAnnotationObj = annotationsMap.get(clickedAnnotationId);		

		const initialType = clickedAnnotationObj.type;
		const initialComment = clickedAnnotationObj.comment;
		const initialConcepts = new Array();
		for (const concept of clickedAnnotationObj.concepts) {
			initialConcepts.push(concept);
		}

		console.log(clickedAnnotationObj);
		getUserInputs(clickedAnnotationObj)
		.then((updatedAnnotationObj) => {
			console.log(initialConcepts);
			console.log(updatedAnnotationObj);
			if (	initialType == updatedAnnotationObj.type &&
					initialComment == updatedAnnotationObj.comment && 
					eqArray(initialConcepts, updatedAnnotationObj.concepts)) {
				throw new Error("Nothing to update!")
			}
			console.log("BEFORE SENDUPDATEREQUEST");
			return sendUpdateRequest(updatedAnnotationObj);
		})
		.then((savedAnnotation) => {
			console.log(savedAnnotation);
			markAnnotation(savedAnnotation);			
			annotationsMap.set(savedAnnotation.id, savedAnnotation);
			console.log(annotationsMap);
			contextMenu.style.display = "none";		
		}).catch((error)=> {
			console.log(error);
			contextMenu.style.display = "none";		
		})						
	}	
}




contextMenu.onclick = function(e) {
	
	console.log("contextMenu->click eventlistener started executing...");

	if (e.target === document.getElementById("drMenuItemHighlight")) {
		annotate(ANNOTATIONTYPE.HIGHLIGHT);
	} else if (e.target === document.getElementById("drMenuItemStrikethrough")) {
		annotate(ANNOTATIONTYPE.STRIKETHROUGH);
	} else if (e.target === document.getElementById("drMenuItemAddTag")) {
		annotate(ANNOTATIONTYPE.TAG);
	} else if (e.target === document.getElementById("drMenuItemDeleteAnnotation")) {
		deleteAnnotation();
	} else if (e.target === document.getElementById("drMenuItemShowAllAnnotations")) {
		showAllAnnotations();
	} else {
		console.log("The clicked menu item could not be detected!")
	}
	contextMenu.style.display = "none";
};



window.onresize = function(e) {
	markAnnotations();
};


	
function annotate (type) {

	let range0 = null;
	if (!document.getSelection().isCollapsed) {
		range0 = document.getSelection().getRangeAt(0);
	} else {
		return;
	}
	
	let startNode = getAncestorWithId(range0.startContainer.parentElement);
	let startOffsetPath = getOffsetPath(startNode, range0.startContainer);
	let startOffsetChar = range0.startOffset;
	let endNode = getAncestorWithId(range0.endContainer.parentElement);
	let endOffsetPath = getOffsetPath(endNode, range0.endContainer);
	let endOffsetChar = range0.endOffset;
	let newAnnotationObj = { 
		//"owner"			: {"id": null, "name": null}, 
		"documentId" 	: documentId, 
		"type"       	: type, 
		"startNodeId"	: startNode.getAttribute("id"), 
		"startOffset"	: startOffsetPath + "-" + startOffsetChar, 
		"endNodeId"  	: endNode.getAttribute("id"), 
		"endOffset"  	: endOffsetPath + "-" + endOffsetChar,
		"comment"	 	: null,
		"concepts"		: new Array()};
			
	getUserInputs(newAnnotationObj)
	.then((newAnnotationObj) => {
		console.log(newAnnotationObj);
		console.log(JSON.stringify(newAnnotationObj));
		return sendCreateRequest(newAnnotationObj)
	})
	.then(response => {
		console.log(response);
		if (!response.ok) {
        	throw new Error("HTTP response status: " + response.status);
		} 			
		return response.json();
	})
	.then(persistedAnnotation=>{
		console.log("newAnnotation persisted: ");
		console.log(persistedAnnotation);
		markAnnotation(persistedAnnotation);
		annotationsMap.set(persistedAnnotation.id, persistedAnnotation);
		contextMenu.style.display = "none";		
	})
	.catch((error) => {
		console.log("Annotate promise chain got broken somewhere...");
 		return error;
	});	
}
		


function sendCreateRequest(newAnnotationObj) {
	return fetch('/rest/annotations/create', {
		method: 'POST', 
		headers: {
			'Accept': 'application/json',
 			'Content-Type': 'application/json; charset=utf-8',
   			'X-CSRF-TOKEN': token
  		},
 		body: JSON.stringify(newAnnotationObj)	
 	})
}



function sendReadRequest() {
	return fetch('/rest/annotations/' + documentId, {
	  method: 'GET'
	})
	.then((response) => { 
		return response.json();
	})
	.catch((error) => {
	  console.error('Error:', error);
	});	
}



function sendUpdateRequest (updatedAnnotationObj) {
	let updatedAnnotation = { 
			"id"		: updatedAnnotationObj.id, 
			"type"		: updatedAnnotationObj.type,
			"comment"	: updatedAnnotationObj.comment,
			"concepts"	: updatedAnnotationObj.concepts};
	console.log(updatedAnnotation);

		return fetch('/rest/annotations/update', 
			{	method: 'PUT', 
				headers: {
					'Accept': 'application/json',
	    			'Content-Type': 'application/json; charset=utf-8',
       				'X-CSRF-TOKEN': token
	  			},
	  			body: JSON.stringify(updatedAnnotation)
			})
	.then(response => {
		if (response.status == 200) {
			return response.json();
		} else {
			throw new Error(response);
		}
	})
	.catch((error) => {
	  return error;
	});	
}




function sendDeleteRequest(annotationId) {
	
	return fetch('/rest/annotations/delete/' + annotationId, {
		method: 'DELETE',
		headers: {
			'Accept': 'application/json',
			'Content-Type': 'application/x-www-form-urlencoded',
        	'X-CSRF-TOKEN': token,
		},
	}).then(response => {
		if (response.status == 200) {
			return response.json();
		} else {
			throw new Error(response);
		}
	})
	.catch((error) => {
	  	console.error('Error deleting annotation: ' + annotationId, error);
		return false;
	});	
}



function deleteAnnotation() {
	let contextAnnotationId = contextMenu.getAttribute("data-context-annotation-id");
	if (contextAnnotationId) {
		let annotationId = Number(contextAnnotationId);
		sendDeleteRequest(contextAnnotationId)
		.then((result)=> {
			console.log("result of sendDeleteRequest is " + result);
			if (result == true) {
			//remove from drAnnotations div			
				contextAnnotations = document.querySelectorAll("div#drAnnotations div[data-annotation-id = '" + contextAnnotationId + "']");
				console.log(contextAnnotations);
				for (const annotation of contextAnnotations) {
					const div = annotation;
					div.remove();
				}
			
				//remove from annotationsMap to prevent reappearing after a screen resizing.
				annotationsMap.delete(annotationId);
			}
			contextMenu.removeAttribute("data-annotation-context-id");
		})

	} else {
		alert ("Please position the pointer on the annotation to be deleted.");
	}
}



function showAllAnnotations() {
	sendReadRequest()
	.then((annotations) => {
		console.log(annotations);
		annotationsMap = new Map();
		for (const annotation of annotations) {
			annotationsMap.set(annotation.id, annotation);
		}
		markAnnotations();
	})	
}



function markAnnotations(){
	annotationsDiv.replaceChildren();
	for (const annotation of annotationsMap.values()) {
		markAnnotation(annotation)
	};
}



function markAnnotation(annotation) {
	//first calculate the range	
	let startNode = document.getElementById(annotation.startNodeId);	
	let offsetArray = annotation.startOffset.split("-");
	let nodeOffset = offsetArray[0];
	let charOffset = offsetArray[1];
	startNode = startNode.childNodes[nodeOffset];
	
	let range = document.createRange();
	range.setStart(startNode, charOffset);

	let endNode = document.getElementById(annotation.endNodeId);
	offsetArray = annotation.endOffset.split("-");
	nodeOffset = offsetArray[0];
	charOffset = offsetArray[1];
	endNode = endNode.childNodes[nodeOffset];
	range.setEnd(endNode, charOffset);

	//then do the real marking of the range.
	markRange(range, annotation);
}


function markRange(range, annotation) {

	let className = "dr" + annotation.type;

    let body = document.body;
    let docEl = document.documentElement;

    let scrollTop = window.pageYOffset || docEl.scrollTop || body.scrollTop;
    let scrollLeft = window.pageXOffset || docEl.scrollLeft || body.scrollLeft;

    let clientTop = docEl.clientTop || body.clientTop || 0;
    let clientLeft = docEl.clientLeft || body.clientLeft || 0;
	
	let rectList = range.getClientRects();
	let annotationDiv = null;	
	annotationsDiv = document.getElementById("drAnnotations");
	
	//clear the existing marks in case this is an update to an existing annotation.
	let annotationIdSelector = "[data-annotation-id='" + annotation.id + "']";
	annotationsDiv.querySelectorAll(annotationIdSelector).forEach(n=>{n.remove()})

	
	for (let i = 0; i < rectList.length; i++) {
		annotationDiv = document.createElement("div");
		annotationsDiv.append(annotationDiv);
		
		//one annotation may span over multiple rectangles, we cant use id as it must be unique.
		annotationDiv.setAttribute("data-annotation-id", annotation.id);
		annotationDiv.setAttribute("data-annotation-owner-id", annotation.owner.id);
		annotationDiv.className = className;
		annotationDiv.style.left = rectList[i].x + scrollLeft - clientLeft + "px";
		annotationDiv.style.top = rectList[i].y +  scrollTop - clientTop + "px";
		annotationDiv.style.width = rectList[i].width + "px";
		annotationDiv.style.height = rectList[i].height + "px";	
		if (annotation.type == ANNOTATIONTYPE.STRIKETHROUGH)	{
			annotationDiv.style.top = rectList[i].y +  scrollTop - clientTop + rectList[i].height/2 + "px";
			annotationDiv.style.height = "2px";	
		}
	}
}



function getAncestorWithId(childNode) {
	if (childNode.hasAttribute("id") || childNode==document) {
		return childNode;
	} else {
		return getAncestorWithId(childNode.parentElement);
	}
}


function getOffsetPath(upperNode, lowerNode) {
	let offsetPath = "";
	let childCount = lowerNode.parentNode.childNodes.length;
	for (let i=0; i<childCount; i++) {
		if (lowerNode.parentNode.childNodes[i]==lowerNode) {
			offsetPath = i.toString(); break;
		}
	}

	if (lowerNode.parentNode==upperNode) {
		return offsetPath;
	} else {
		offsetPath = getOffsetPath(upperNode, lowerNode.parentNode) + " " + offsetPath;
		return offsetPath;
	}
}



function dragElement(el) {
	let shiftX = 0, shiftY = 0, mouseX = 0, mouseY = 0;
	el.onmousedown = dragInit;
	function dragInit(e) {
		e = e || window.event;
		//e.preventDefault();

		mouseX = e.clientX;
		mouseY = e.clientY;
		
		if (e.target.tagName == "TEXTAREA" ) {
			return;
		}
		//console.log(e.target);
		el.onmousemove = drag;
		el.onmouseup = dragRelease;
	}

	function drag(e) {

		e = e || window.event;
		e.preventDefault();

		// calculate the shift:
		shiftX = e.clientX - mouseX;
		shiftY = e.clientY - mouseY;
		mouseX = e.clientX;
		mouseY = e.clientY;

		// set the element's new position:
		el.style.left = (el.offsetLeft + shiftX) + "px";
		el.style.top  = (el.offsetTop  + shiftY) + "px";
	}

	function dragRelease() {
		// stop moving when mouse button is released:
		el.onmouseup = null;
		el.onmousemove = null;
	}	
}
dragElement(document.querySelector("#drAnnotationDialogContent"));



function getUserInputs(annotationObj) {
	let dialog      = document.getElementById('drAnnotationDialog');
	let infoElm		= document.getElementById("drAnnotationInfo");
	let commentElm 	= document.getElementById('drAnnotationComment');
	let tagsElm    	= document.getElementById('drAnnotationTags');
	let okButton    = document.getElementById('drAnnotationOk');
	let cancelButton= document.getElementById('drAnnotationCancel');
	
	infoElm.textContent = "-";
	if (annotationObj.id) {
		infoElm.textContent = "(" + annotationObj.owner.name + "-" + annotationObj.id + ")";
	}
		
	switch (annotationObj.type) {
	case ANNOTATIONTYPE.HIGHLIGHT: 
		document.getElementById("drHighlightRadio").checked = true;
		commentElm.value = annotationObj.comment;
		commentElm.style.display='block'; 
		tagsElm.style.display='none';
		break;	
	case ANNOTATIONTYPE.STRIKETHROUGH: 
		document.getElementById("drStrikethroughRadio").checked = true;
		commentElm.value = annotationObj.comment;
		commentElm.style.display='block'; 
		tagsElm.style.display='none';
		break;	
	case ANNOTATIONTYPE.TAG: 
		document.getElementById("drTagRadio").checked = true;
		tagsElm.value = null;
		for (const element of annotationObj.concepts) {console.log(annotationObj); console.log(element); console.log(element.concept); tagsElm.value = tagsElm.value.concat(element.concept.name, ", ")};
		tagsElm.value = tagsElm.value.replace(/, $/, '');
		commentElm.style.display='none'; 
		tagsElm.style.display='block';
		break;
	default:
		alert("annotationType unknown!!!");
		return;
	}

	
	dialog.style.display="block";

	return new Promise(function(resolve, reject) {
		dialog.addEventListener('click', function handleButtonClicks(e) {	
				
			if ((e.target !== okButton) && (e.target !== cancelButton)) { 
				return; 
			}
			dialog.removeEventListener('click', handleButtonClicks);
			dialog.style.display="none";
			
			if (e.target === okButton) {
				let annotationTypeRadio = document.querySelector('input[name="drAnnotationType"]:checked');
				console.log(annotationObj);
				if (annotationTypeRadio.value == ANNOTATIONTYPE.TAG) {
					annotationObj.type = annotationTypeRadio.value;
					annotationObj.comment = null;
					let userTagsArray = tagsElm.value.split(",");
					for (let i=0; i<userTagsArray.length; i++) {
						console.log("before:" + userTagsArray[i]);
						userTagsArray[i] = userTagsArray[i].trim();
						console.log("after:" + userTagsArray[i]);
					}
  					annotationObj.concepts = new Array(); //clear
					annotationObj.concepts = annotationObj.concepts.concat(userTagsArray) ;								
				} else {
  					annotationObj.type = annotationTypeRadio.value;
					annotationObj.comment = commentElm.value;
  					annotationObj.concepts = null;
				}
				console.log(annotationObj);
				resolve(annotationObj);
			} else { //(e.target === cancelButton) 
				reject(new Error('User cancelled'));
			} 
		});
	});
}

