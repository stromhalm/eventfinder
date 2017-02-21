$(function () {

	// Configure the GOOGLE MAPS API
	var useragent = navigator.userAgent;
	if (useragent.indexOf('iPhone') != -1 || useragent.indexOf('Android') != -1) {
		var mapOptions = {
			zoom: 14,
			center: new google.maps.LatLng(53.1462326, 8.2209063), // location of Oldenburg
			keyboardShortcuts: false,
			streetViewControl: false,
			mapTypeControl: false,
			zoomControl: false
		};
	} else {
		var mapOptions = {
			zoom: 14,
            streetViewControl: false,
            center: new google.maps.LatLng(53.1462326, 8.2209063), // location of Oldenburg
			keyboardShortcuts: false
		};
	}

	map = new google.maps.Map(document.getElementById('mapCanvas'), mapOptions);

	var myLocation = false;
	google.maps.event.addDomListener(document.getElementById('locateMeButton'), 'click', function () {
		var icon = {
			url: '/Images/position.png', // url
			scaledSize: new google.maps.Size(25, 25), // scaled size
			origin: new google.maps.Point(0, 0), // origin
			anchor: new google.maps.Point(0, 0) // anchor
		};
		if (!myLocation) {
			myLocation = new google.maps.Marker({
				clickable: false,
				icon: icon,
				animation: google.maps.Animation.DROP,
				map: map
			});
		}

		if (navigator.geolocation) navigator.geolocation.getCurrentPosition(function (pos) {
			var me = new google.maps.LatLng(pos.coords.latitude, pos.coords.longitude);
			myLocation.setPosition(me);
			map.panTo(myLocation.getPosition());
		}, function (error) {});
	});
	locateMeButton.index = 1;
	map.controls[google.maps.ControlPosition.BOTTOM_CENTER].push(locateMeButton);


	var oms = new OverlappingMarkerSpiderfier(map);
	var iw = new google.maps.InfoWindow();

	oms.addListener('click', function (marker, event) {
		iw.setContent(marker.infoText);
		iw.open(map, marker);
	});

	var directionRequest = {travelMode: google.maps.TravelMode.DRIVING};
	/**GOOGLE-DIRECTION**/
	var directionsRenderer = new google.maps.DirectionsRenderer({suppressMarkers: true}); // hide the markers created by the navigation
	directionsRenderer.setMap(map);
	var directionService = new google.maps.DirectionsService();

	var markers = [];

	var searchCache = " ";
	var yourPositionMarker;
	var categoryCache = "";
	var showFirstResult = false;

	/** Initially show ALL markers **/
	search("", "");
	$(".tabs").hide();

	/*****************/
	/*Site Navigation*/
	/*****************/

	// Navigate via arrow keys
	$(document).keydown(function (key) {

		var firstTab = document.getElementById('tab1');
		var secondTab = document.getElementById('tab2');
		var thirdTab = document.getElementById('tab3');

		if (firstTab.checked) {
			switch (key.which) {

				case 13: // enter
					openMarker($('.resultList.selected').attr('data-resultnr'));
					break;

				case 38: // up

					if (parseInt($('.resultList.selected').attr('data-resultnr')) == (parseInt($('.resultList').length)) - 1) {
						break;
					}
					if ($('.resultList.selected').length > 0) {
						var nextSelected = parseInt($('.resultList.selected').attr('data-resultnr')) + 1;
						$('.resultList.selected').removeClass('selected'); // Deselect current
						$('.resultList[data-resultnr=' + nextSelected + ']').addClass('selected') // Select next
						jQuery('.tab-content').scrollTo('.selected');

					}
					break;

				case 40: // down

					$('#tabLabel1').addClass('tabSelected');

					$(".menu").hide();
					$(".tabs").show();

					if (parseInt($('.resultList.selected').attr('data-resultnr')) == 0) {
						break;
					}
					if ($('.resultList.selected').length == 0) {
						$('.resultList[data-resultnr=' + ((parseInt($('.resultList').length)) - 1) + ']').addClass('selected');
					} else {
						var nextSelected = parseInt($('.resultList.selected').attr('data-resultnr')) - 1;
						$('.resultList.selected').removeClass('selected'); // Deselect current
						$('.resultList[data-resultnr=' + nextSelected + ']').addClass('selected') // Select next
						jQuery('.tab-content').scrollTo('.selected');

					}

					break;

				case 39: //right

					document.getElementById('tab1').checked = false;
					document.getElementById('tab2').checked = true;

					break;

                case 27: //escape
                    $(".tabs").hide();
                    $(".menu").hide();
                    $("article").fadeOut('fast');
                    break;

				default:
					return; // exit this handler for other keys
			}
		} else if (secondTab.checked) {
			switch (key.which) {

				case 37: //left
					if ($('#cat1_3').hasClass('selected')) {
						$('.categoryButton').removeClass('selected');
						$('#cat1_2').addClass('selected');

					} else if ($('#cat1_2').hasClass('selected')) {
						$('.categoryButton').removeClass('selected');
						$('#cat1_1').addClass('selected');

					} else if ($('#cat1_1').hasClass('selected')) {
						$('.categoryButton').removeClass('selected');
						$('#tab2').prop('checked', false);
						$('#tab1').prop('checked', true);

					} else if ($('#cat2_3').hasClass('selected')) {
						$('.categoryButton').removeClass('selected');
						$('#cat2_2').addClass('selected');

					} else if ($('#cat2_2').hasClass('selected')) {
						$('.categoryButton').removeClass('selected');
						$('#cat2_1').addClass('selected');

					} else if ($('#cat2_1').hasClass('selected')) {
						$('.categoryButton').removeClass('selected');
						$('#tab2').prop('checked', false);
						$('#tab1').prop('checked', true);

					} else if ($('#cat3_3').hasClass('selected')) {
						$('.categoryButton').removeClass('selected');
						$('#cat3_2').addClass('selected');

					} else if ($('#cat3_2').hasClass('selected')) {
						$('.categoryButton').removeClass('selected');
						$('#cat3_1').addClass('selected');

					} else {
						$('.categoryButton').removeClass('selected');
						$('#tab2').prop('checked', false);
						$('#tab1').prop('checked', true);
					}
					break;
				case 39: //right
					if ($('#cat1_1').hasClass('selected')) {
						$('.categoryButton').removeClass('selected');
						$('#cat1_2').addClass('selected');

					} else if ($('#cat1_2').hasClass('selected')) {
						$('.categoryButton').removeClass('selected');
						$('#cat1_3').addClass('selected');

					} else if ($('#cat1_3').hasClass('selected')) {
						$('.categoryButton').removeClass('selected');
						$('#tab2').prop('checked', false);
						$('#tab3').prop('checked', true);

					} else if ($('#cat2_1').hasClass('selected')) {
						$('.categoryButton').removeClass('selected');
						$('#cat2_2').addClass('selected');

					} else if ($('#cat2_2').hasClass('selected')) {
						$('.categoryButton').removeClass('selected');
						$('#cat2_3').addClass('selected');

					} else if ($('#cat2_3').hasClass('selected')) {
						$('#tab2').prop('checked', false);
						$('#tab3').prop('checked', true);
						$('.categoryButton').removeClass('selected')

					} else if ($('#cat3_1').hasClass('selected')) {
						$('.categoryButton').removeClass('selected');
						$('#cat3_2').addClass('selected');

					} else if ($('#cat3_2').hasClass('selected')) {
						$('.categoryButton').removeClass('selected');
						$('#cat3_3').addClass('selected');

					} else {
						$('.categoryButton').removeClass('selected');
						$('#tab2').prop('checked', false);
						$('#tab3').prop('checked', true);
					}

					break;
				case 38: // up
					if ($('#cat1_1').hasClass('selected')) {
						$('.categoryButton').removeClass('selected')
					} else if ($('#cat2_1').hasClass('selected')) {
						$('.categoryButton').removeClass('selected');
						$('#cat1_1').addClass('selected');
					} else if ($('#cat3_1').hasClass('selected')) {
						$('.categoryButton').removeClass('selected');
						$('#cat2_1').addClass('selected');
					} else if ($('#cat1_2').hasClass('selected')) {
						$('.categoryButton').removeClass('selected')
					} else if ($('#cat2_2').hasClass('selected')) {
						$('.categoryButton').removeClass('selected');
						$('#cat1_2').addClass('selected');
					} else if ($('#cat3_2').hasClass('selected')) {
						$('.categoryButton').removeClass('selected');
						$('#cat2_2').addClass('selected');
					} else if ($('#cat1_3').hasClass('selected')) {
						$('.categoryButton').removeClass('selected');
					} else if ($('#cat2_3').hasClass('selected')) {
						$('.categoryButton').removeClass('selected');
						$('#cat1_3').addClass('selected');
					} else if ($('#cat3_3').hasClass('selected')) {
						$('.categoryButton').removeClass('selected');
						$('#cat2_3').addClass('selected');
					}

					break;
				case 40: //down
					if ($('#cat1_1').hasClass('selected')) {
						$('.categoryButton').removeClass('selected');
						$('#cat2_1').addClass('selected');
					} else if ($('#cat2_1').hasClass('selected')) {
						$('.categoryButton').removeClass('selected');
						$('#cat3_1').addClass('selected');
					} else if ($('#cat3_1').hasClass('selected')) {
						break;
					} else if ($('#cat1_2').hasClass('selected')) {
						$('.categoryButton').removeClass('selected');
						$('#cat2_2').addClass('selected');
					} else if ($('#cat2_2').hasClass('selected')) {
						$('.categoryButton').removeClass('selected');
						$('#cat3_2').addClass('selected');
					} else if ($('#cat1_3').hasClass('selected')) {
						$('.categoryButton').removeClass('selected');
						$('#cat2_3').addClass('selected');
					} else if ($('#cat2_3').hasClass('selected')) {
                        $('.categoryButton').removeClass('selected');
                        $('#cat3_3').addClass('selected');
                    } else if ($('#cat3_2').hasClass('selected')) {
                        break;
                    } else if ($('#cat3_3').hasClass('selected')){
                        break;
					} else {
                        $(".tabs").show();
						$('#cat1_2').addClass('selected')
					}
					break;

				case 13: // enter

					$.each($('input[name="category"]'), function (key, data) {

						if ($('label[for=\'' + data.id + '\']').hasClass('selected')) {

                            if($('#' + data.id).is(':checked')) {

                                $(this).removeClass('categoryActive');

                                $("nav").removeClass("categoryEnabled");
                                $('#' + data.id).prop('checked', false);
                                $("#tab1").prop("checked", true);

                                // Start search without category
                                search($("#search").val(), '');
                            } else {

                                $.each($('input[name="category"]'), function () {
                                    $(this).removeClass('categoryActive');
                                });

                                // check the radio and set category text
                                $('#' + data.id).prop('checked', true);

                                $(".categoryHTML").html($('label[for=\'' + data.id + '\']').html());
                                $("nav").addClass("categoryEnabled"); // Make search smaller
                                $("#tab1").prop("checked", true);

                                // fire post, if query with category has changed
                                if ($("#search").val() != searchCache || $('input[name="category"]:checked').val() != categoryCache) { //Has the query changed?
                                    search($("#search").val(), $('input[name="category"]:checked').val());
                                }
                                $(this).addClass('categoryActive');

                            }
                            $('label[for=\'' + data.id + '\']').removeClass('selected');
						}
					});
					break;

                case 27: //escape
                    $('.categoryButton').removeClass('selected')

                    $(".tabs").hide();
                    $(".menu").hide();
                    $("article").fadeOut('fast');
                    break;
			}


		} else { //thirdTab.checked

			switch (key.which) {

				case 37: //left

					if ($('#city').is(":focus")) {
						$('#activity').focus();
						break;
					}
					else {
						$('#tab3').prop('checked', false);
						$('#tab2').prop('checked', true);
					}
					break;
				case 39: //right
					if ($('#activity').is(":focus")) {
						$('#city').focus();
						break;
					} else {
						break;
					}

					break;

				case 40: //down
                    if ($(".tabs").is(":visible")){
                        $('#activity').focus();
                    } else {
                        $(".tabs").show();
                    }
					break;

                case 13: //enter
                    showFirstResult = true;
                    search($("#activity").val() + " " + $("#city").val(), "");

                    break;

                case 27: //escape
                    $(".tabs").hide();
                    $(".menu").hide();
                    $("article").fadeOut('fast');
                    break;
			}


		}


	});

	//function to autoscroll through results when navigating with keyboard
	//source: http://lions-mark.com/jquery/scrollTo/
	$.fn.scrollTo = function (target, options, callback) {
		if (typeof options == 'function' && arguments.length == 2) {
			callback = options;
			options = target;
		}
		var settings = $.extend({
			scrollTarget: target,
			offsetTop: 200,
			duration: 150,
			easing: 'linear'
		}, options);
		return this.each(function () {
			var scrollPane = $(this);
			var scrollTarget = (typeof settings.scrollTarget == "number") ? settings.scrollTarget : $(settings.scrollTarget);
			var scrollY = (typeof scrollTarget == "number") ? scrollTarget : scrollTarget.offset().top + scrollPane.scrollTop() - parseInt(settings.offsetTop);
			scrollPane.animate({
				scrollTop: scrollY
			}, parseInt(settings.duration), settings.easing, function () {
				if (typeof callback == 'function') {
					callback.call(this);
				}
			});
		});
	};

	// Toggle mobile menu
	$(".showMenu").click(function () {
		$(".tabs").hide();
		$(".menu").toggle();
	});

	$("#mapCanvas").click(function () {
		$(".tabs").hide();
		$(".menu").hide();
		$("article").fadeOut('fast');
	});

	// Open content windows (about, share, ...)
	$(".openContent").click(function () {

		$(".tabs").hide();
		$(".menu").hide();

		var contentWindow = $(this);

		$("article").fadeOut('fast').promise().done(function () {
			$(contentWindow.attr('data-href')).fadeIn('fast');
		});
	});

	// Close content windows
	$(".close a").click(function () {
		$(".menu").hide();
		$("article").fadeOut('fast');
	});

	/*****************************/
	/*Searchbar and User Requests*/
	/*****************************/

	$('#feelingLucky').click(function () {
		showFirstResult = true;
		search($("#activity").val() + " " + $("#city").val(), "");
	});

	executeAfterSearch = function () {
		if (showFirstResult == true) {
			if (markers.length > 0) {
				openMarker(0);
			} else {
				alert("Leider keine passenden Events gefunden!");
			}
			showFirstResult = false;
		}
	};

	$("#search").click(function () {
		$(".menu").hide();
		$(".tabs").show();
		$("article").fadeOut('fast');
		if ($("#search").val() != searchCache || $('input[name="category"]:checked').val() != categoryCache) { //Has the query changed?
			search($("#search").val(), $('input[name="category"]:checked').val());
		}
	});

	$("#search").keyup(function (event) {
		$(".menu").hide();
		if (event.keyCode != '13') { // Do not fire when enter is pressed
			if ($("#search").val() != searchCache || $('input[name="category"]:checked').val() != categoryCache) { //Has the query changed?
				search($("#search").val(), $('input[name="category"]:checked').val());
			}
		}
	});

     $('.categoryActive').click( function () {
         $("nav").removeClass("categoryEnabled");
         $("input[name='category']:checked").prop("checked", false); // Clear invisible radio buttons
         search($("#search").val(), '');
     });

    //Click event for categories
	$('input[name="category"]:radio').click(function () {

        if ($(this).hasClass('categoryActive')){
            $("nav").removeClass("categoryEnabled");

            $(this).prop("checked", false);
            $(this).removeClass('categoryActive');

            search($("#search").val(), '');

        } else {
            $.each($('input[name="category"]'), function () {
                $(this).removeClass('categoryActive');
            });

            var html = $("label[for='" + $(this).attr('id') + "']").html();

            $(".categoryHTML").html(html);
            $("nav").addClass("categoryEnabled");
            $("#tab1").prop("checked", true);

            if ($("#search").val() != searchCache || $('input[name="category"]:checked').val() != categoryCache) { //Has the query changed?
                search($("#search").val(), $('input[name="category"]:checked').val());
            }

            $(this).addClass('categoryActive');
        }

    });

	function search(query, category) {

		searchCache = query; // refresh cache
		categoryCache = category; //refresh cache

		$.ajax({
			url: "/api/search",
			type: 'POST',
			dataType: 'json',
			data: {
				searchText: query,
				category: category
			},
			success: function (results) {
				if (results[0] != undefined) {
					showResults(results);
					executeAfterSearch();
				} else {
					showEmptyResultMessage();
				}
			}
		});
	}

	function showEmptyResultMessage() {

		// eliminate previous results
		$("#tab-content1").empty();

		// flush markers from the map and the array in addition to previous locationResults
		$.each(markers, function (index, value) {
			value.setMap(null);
		});

		// Clear markers & windows
		markers = [];

		$("#tab-content1").appendAt(
			'<div class="result resultList">' +
			'<div>' +
			'<h2 style="line-height: 25px; padding-left: 10px"> Deine Suche ergab leider keine Treffer. </h2>' +
			'<p style="padding-left: 10px"> Sieh nach ob du dich eventuell verschrieben hast... </p>' +
			'</div>' +
			'</div>'
			, 0
		);
	}

	function showResults(results) {

		// eliminate previous results
		$("#tab-content1").empty();

		// flush markers from the map and the array in addition to previous locationResults
		$.each(markers, function (index, value) {
			value.setMap(null);
		});

		// Clear markers & windows
		markers = [];

		// append results with each-loop
		$.each(results, function (key, data) {

			//Colour for event markers
			if (data.isToday) {
				var markerIcon = '/Images/marker-today.png';
			} else if (data.isTomorrow) {
				var markerIcon = '/Images/marker-tomorrow.png';
			} else if (data.isInNextThreeDays) {
				var markerIcon = '/Images/marker-three-days.png';
			} else if (data.isNextWeek) {
				var markerIcon = '/Images/marker-next-week.png';
			} else {
				var markerIcon = '/Images/marker-far-away.png';
			}

			var infoBlock =
				'<a class="sideButton showDirection" href="#"><img src="/Images/route.png" alt="directions"> Route</a>' +
				'<div class="openMarker">' +
				'<img class="marker" src="' + markerIcon + '">' +
				'<h4>' + data.date + '</h4>' +
				'<h3>' + data.title + '</h3>' +
				'<h5>' + data.location + '</h5>' +
				'<p >' + data.description + '</p>' +
				'<a target="_blank" href="https://www.facebook.com/sharer/sharer.php?u=' + encodeURIComponent(data.infoUrl) + '" class="like" data-shares="' + data.likes + '"><span class="glyphicon glyphicon-share"></span> Teilen - [' + data.likes + ']</a>' +
				'<a class="sourceLink" style="color:#4889F3;" href="' + data.infoUrl + '" target="_blank">Quelle</a>' +
				'</div>';

			// append results to the list
			$("#tab-content1").appendAt(
				'<div class="result resultList" data-resultNr="' + key + '">' +
				infoBlock +
				'</div>'
				, 0
			);
			// append marker on map
			var marker = new google.maps.Marker({
				position: new google.maps.LatLng(data.latitude, data.longitude),
				animation: google.maps.Animation.DROP,
				map: map,
				title: data.title,
				icon: markerIcon
			});

			// create info-window for the marker
			marker.infoText = '<div class="result infoWindow" data-resultNr="' + key + '">' +
				'<a class="sideButton openNavigation" target="_blank" href="#"><span class="glyphicon glyphicon-new-window"></span><br/>Navigation</a>'
				+ infoBlock +
				'</div>';

			// add marker-listener
			oms.addListener('click', function (marker, event) {
				$('#mapCanvas').removeClass('navMode');
			});

			markers.push(marker);  // store marker to delete on new input
			oms.addMarker(marker);
		});

	}

	jQuery.fn.appendAt = function (content, index) {
		this.each(function (i, item) {
			var $content = $(content).clone();
			if (index === 0) {
				$(item).prepend($content);
			} else {
				$content.insertAfter($(item).children().eq(index - 1));
			}
		});
		$(content).remove();
		return this;
	};

	/*************/
	/*Google Maps*/
	/*************/

	// increase the facebook count on click
	$(document).on('click', '.like', function () {
		var parent = $(this).parent();
		var date = $($(parent).find("h4")).html();
		var location = $($(parent).find("h5")).html();
		var dataShares = $(this).attr("data-shares");

		var target = $(this).attr("href");

		var eventNr = $($(this).parent()).parent().attr('data-resultNr');

		$.ajax({
			url: "/api/shares",
			type: 'POST',
			data: {
				date: date,
				location: location,
				shares: dataShares
			},
			success: function (result) {
				$("a[href='" + target + "']").html('Teilen - [' + result.shares + ']');
				$("a[href='" + target + "']").attr("data-shares", result.shares);
				markers[eventNr].infoText = markers[eventNr].infoText.replace('data-shares=\"' + (result.shares - 1) + '\"', 'data-shares=\"' + result.shares + '\"');
				markers[eventNr].infoText = markers[eventNr].infoText.replace('Teilen - [' + (result.shares - 1) + ']', 'Teilen - [' + result.shares + ']');
			}
		});
	});

	// Use external routing service for turn-by-turn-directions
	function setMapExport(location, latlng) {

		latlng = latlng.toString().replace(' ', '');
		latlng = latlng.replace('(', '');
		latlng = latlng.replace(')', '');

		// check which device our page is running on
		if (useragent.match(/iPad/i) || useragent.match(/iPhone/i) || useragent.match(/iPod/i)) { // the /i indicates that there will be an integer after the string
			routeString = 'maps://maps.apple.com/?q="' + latlng;
		} else if (useragent.match(/Android/i)) {
			routeString = 'geo:' + latlng + '?q=' + latlng;
		} else {
			routeString = 'http://maps.google.com/maps?saddr=(' + location.coords.latitude + ',' + location.coords.longitude + ')&daddr=' + latlng;
		}
		$('.openNavigation').attr('href', routeString);
	}

	// jump to the marker of the selected event and open infoWindow
	$(document).on('click', '.openMarker', function () { // use on for dynamically added results
		openMarker($(this).parent().attr('data-resultNr'));
	});

	// jump to the marker of the selected event and open infoWindow
	function openMarker(id) {

		var marker = markers[id];
		map.panTo(marker.getPosition()); // pan animates the jump and is better than setMap

		$('#mapCanvas').removeClass('navMode');
		iw.setContent(marker.infoText);
		iw.open(map, marker);

		$(".tabs").hide();
	}

	// show the route to the event from the current position
	$(document).on('click', '.showDirection', function () {

		$(".tabs").hide();

		// get the the destination position
		var eventNr = $(this).parent().attr('data-resultNr');
		var marker = markers[eventNr];

		iw.setContent(marker.infoText);
		iw.open(map, marker);
		directionRequest.destination = markers[eventNr].getPosition();

		// ask current location and show route
		if (navigator.geolocation) {
			navigator.geolocation.getCurrentPosition(showRoute, showError); // sends the answer and results to the handle

			// Show app link
			$('#mapCanvas').addClass("navMode");
		} else {
			alert("Dein Browser unterstützt leider keine geoLocation");
		}
	});

	// callback-funtction of the geoLocation
	function showRoute(location) {

		// append marker on map and flush the last position --> can be another than a minute ago
		if (yourPositionMarker) {
			yourPositionMarker.setMap(null);
		}

		yourPositionMarker = new google.maps.Marker({
			position: new google.maps.LatLng(location.coords.latitude, location.coords.longitude),
			map: map,
			title: 'Deine Position',
			icon: '/Images/position.png' // Blue dot
		});

		// show the route
		directionRequest.origin = new google.maps.LatLng(location.coords.latitude, location.coords.longitude);
		directionService.route(directionRequest, function (result, status) {
			if (status == google.maps.DirectionsStatus.OK) {
				directionsRenderer.setDirections(result);
			}
		});

		setMapExport(location, directionRequest.destination);
	}

	function showError(error) {
		switch (error.code) {
			case error.PERMISSION_DENIED:
				alert("Bitte erlauben Sie den Zugriff auf Ihre Position");
				break;
			default:
				alert("Ihre Position ist gerade nicht verfügbar");
		}
	}
});