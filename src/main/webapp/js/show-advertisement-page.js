
document.addEventListener("DOMContentLoaded", function(event) {

    document.getElementById("idAdvFeedback").setAttribute("value", getIdAdvertisement());
    document.getElementById("idAdvBooking").setAttribute("value", getIdAdvertisement());
    fetchAdvertisement();
    fetchRate();
    fetchFeedbackList();
    fetchBookingList();
    fetchImageList();
});

function getIdAdvertisement() {
    let url = window.location.href;
    url = url.substring(
        url.lastIndexOf("adv-show/") + 9
    );
    // TODO Checks if the ID makes sense
    return url;
}

function fetchAdvertisement() {
    let url = new URL(contextPath+"/adv/" + getIdAdvertisement());
    sendJsonRequest(url,"GET","",loadAdvertisement());
}

function loadAdvertisement(req) {
    // TODO write function body
}

function fetchRate() {
    let url = new URL(contextPath+"/adv/" + getIdAdvertisement()+"/rate");
    sendJsonRequest(url,"GET","",loadRate());
}

function loadRate() {
    // TODO write function body
}

function fetchFeedbackList() {
    let url = new URL(contextPath+"/adv/" + getIdAdvertisement()+"/feedback");
    sendJsonRequest(url,"GET","",loadFeedbackList());
}

function loadFeedbackList() {
    // TODO write function body
}

function fetchBookingList() {
    let url = new URL(contextPath+"/adv/" + getIdAdvertisement()+"/booking");
    sendJsonRequest(url,"GET","",loadBookingList());
}

function loadBookingList() {
    // TODO write function body
}

function fetchImageList() {
    let url = new URL(contextPath+"/adv/" + getIdAdvertisement()+"/image");
    sendJsonRequest(url,"GET","",loadImageList());
}

function loadImageList() {
    // TODO write function body
}