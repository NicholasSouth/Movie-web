// Broken or missing posters must not leave an empty, collapsed movie card.
document.querySelectorAll('img[data-fallback]').forEach(function (image) {
    function fallback() {
        var target = image.getAttribute('data-fallback');
        if (target && image.getAttribute('src') !== target) image.setAttribute('src', target);
    }
    image.addEventListener('error', fallback, { once: true });
    if (image.complete && image.naturalWidth === 0) fallback();
});
