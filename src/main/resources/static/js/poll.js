document.addEventListener('DOMContentLoaded', function () {
  document.querySelectorAll('.poll-btn').forEach(function (btn) {
    btn.addEventListener('click', function () {
      var pollId = btn.getAttribute('data-poll-id');
      var option = btn.getAttribute('data-option');
      btn.disabled = true;
      submitVote(pollId, option);
    });
  });
});

function getCsrfHeaders() {
  var tokenMeta = document.querySelector('meta[name="_csrf"]');
  var headerMeta = document.querySelector('meta[name="_csrf_header"]');
  var headers = { 'Content-Type': 'application/x-www-form-urlencoded' };
  if (tokenMeta && headerMeta && tokenMeta.content && headerMeta.content) {
    headers[headerMeta.content] = tokenMeta.content;
  }
  return headers;
}

function submitVote(pollId, option) {
  fetch('/poll/vote', {
    method: 'POST',
    headers: getCsrfHeaders(),
    body: 'pollId=' + encodeURIComponent(pollId) + '&option=' + encodeURIComponent(option)
  })
    .then(function (res) {
      return res.json().then(function (data) {
        return { ok: res.ok || res.status === 409, data: data };
      });
    })
    .then(function (result) {
      if (result.ok) {
        renderPollResults(result.data);
      } else {
        alert('Something went wrong submitting your vote. Please try again.');
        document.querySelectorAll('.poll-btn').forEach(function (b) { b.disabled = false; });
      }
    })
    .catch(function () {
      alert('Network error — please try again.');
      document.querySelectorAll('.poll-btn').forEach(function (b) { b.disabled = false; });
    });
}

function renderPollResults(data) {
  var buttonsEl = document.getElementById('poll-buttons');
  var resultsEl = document.getElementById('poll-results');
  var noteEl = document.getElementById('poll-voted-note');

  if (buttonsEl) buttonsEl.style.display = 'none';
  if (resultsEl) resultsEl.style.display = 'block';
  if (noteEl) noteEl.style.display = 'block';

  var pie = document.getElementById('poll-pie');
  if (pie) {
    pie.style.background = 'conic-gradient(var(--color-rose) 0% ' + data.percentA + '%, var(--color-sage) ' +
      data.percentA + '% 100%)';
  }

  var legendA = document.getElementById('poll-legend-a');
  var legendB = document.getElementById('poll-legend-b');
  if (legendA) legendA.textContent = data.optionAText + ' — ' + data.percentA + '% (' + data.countA + ')';
  if (legendB) legendB.textContent = data.optionBText + ' — ' + data.percentB + '% (' + data.countB + ')';

  var totalEl = document.getElementById('poll-total');
  if (totalEl) totalEl.textContent = data.total + (data.total === 1 ? ' response' : ' responses');
}
