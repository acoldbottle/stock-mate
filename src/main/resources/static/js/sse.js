let eventSource;

function connectSSE() {
    if (eventSource && eventSource.readyState !== EventSource.CLOSED) {
        return;
    }
    eventSource = new EventSource("/sse/connect");

    eventSource.onerror = function (event) {
        eventSource.close();
    };
}

function disconnectSSE(event) {
    event.preventDefault();

    if (eventSource) {
        eventSource.close();
    }

    fetch('/sse/disconnect', { method: 'POST' })
        .finally(() => {
            event.target.form.submit();
        });
}


document.addEventListener("DOMContentLoaded", connectSSE);