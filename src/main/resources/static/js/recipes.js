// Client-side filtering for the Recipes page.
//   1. Title search  — a card matches when its title contains the query.
//   2. "What's in my fridge?" — a typeahead multi-select of ingredients; a card
//      matches when its top-five ingredients include at least two selected ones.
// Both filters combine with AND: a card is shown only when it passes both.
(function () {
  var grid = document.getElementById('recipeGrid');
  if (!grid) return;

  var cards = Array.prototype.slice.call(grid.querySelectorAll('.recipe-card'));
  var titleSearch = document.getElementById('titleSearch');
  var noResults = document.getElementById('noResults');
  var recipeCount = document.getElementById('recipeCount');

  var fridge = document.getElementById('fridge');
  var fridgeInput = document.getElementById('fridgeInput');
  var fridgeMenu = document.getElementById('fridgeMenu');
  var fridgeChips = document.getElementById('fridgeChips');
  var fridgeNoMatch = document.getElementById('fridgeNoMatch');
  var options = Array.prototype.slice.call(fridgeMenu.querySelectorAll('li[data-value]'));

  var selected = []; // ingredient values currently chosen (lowercase)

  var MIN_MATCHES = 2;

  function applyFilters() {
    var query = (titleSearch.value || '').trim().toLowerCase();
    var visible = 0;

    cards.forEach(function (card) {
      var title = card.getAttribute('data-title') || '';
      var ingredients = (card.getAttribute('data-ingredients') || '')
        .split('|')
        .filter(Boolean);

      var titleMatch = !query || title.indexOf(query) !== -1;

      var fridgeMatch = true;
      if (selected.length > 0) {
        var hits = selected.filter(function (s) {
          return ingredients.indexOf(s) !== -1;
        }).length;
        fridgeMatch = hits >= MIN_MATCHES;
      }

      var show = titleMatch && fridgeMatch;
      card.hidden = !show;
      if (show) visible++;

      // Bold the card's ingredients that the user has selected from the fridge menu.
      var lis = card.querySelectorAll('.recipe-card-ingredients li');
      Array.prototype.forEach.call(lis, function (li) {
        var value = li.getAttribute('data-value');
        li.classList.toggle('matched', selected.indexOf(value) !== -1);
      });
    });

    noResults.hidden = visible !== 0;

    if (recipeCount) {
      recipeCount.textContent = 'Showing ' + visible + ' of ' + cards.length +
        (cards.length === 1 ? ' recipe' : ' recipes');
    }
  }

  // ---- Fridge typeahead ----
  function openMenu() {
    fridgeMenu.hidden = false;
    fridgeInput.setAttribute('aria-expanded', 'true');
  }

  function closeMenu() {
    fridgeMenu.hidden = true;
    fridgeInput.setAttribute('aria-expanded', 'false');
  }

  function filterMenu() {
    var q = (fridgeInput.value || '').trim().toLowerCase();
    var shown = 0;
    options.forEach(function (opt) {
      var value = opt.getAttribute('data-value');
      var isSelected = selected.indexOf(value) !== -1;
      var matches = !q || value.indexOf(q) !== -1;
      var show = matches && !isSelected;
      opt.hidden = !show;
      if (show) shown++;
    });
    if (fridgeNoMatch) fridgeNoMatch.hidden = shown !== 0;
  }

  function renderChips() {
    // Rebuild the chip row from the selected list.
    Array.prototype.slice.call(fridgeChips.querySelectorAll('.fridge-chip'))
      .forEach(function (c) { c.remove(); });

    selected.forEach(function (value) {
      var opt = options.filter(function (o) { return o.getAttribute('data-value') === value; })[0];
      var label = opt ? opt.textContent : value;

      var chip = document.createElement('span');
      chip.className = 'fridge-chip';
      chip.textContent = label;

      var remove = document.createElement('button');
      remove.type = 'button';
      remove.className = 'fridge-chip-remove';
      remove.setAttribute('aria-label', 'Remove ' + label);
      remove.textContent = '×';
      remove.addEventListener('click', function (e) {
        e.stopPropagation();
        deselect(value);
      });

      chip.appendChild(remove);
      fridgeChips.appendChild(chip);
    });
  }

  function select(value) {
    if (selected.indexOf(value) === -1) {
      selected.push(value);
      renderChips();
      applyFilters();
    }
    fridgeInput.value = '';
    filterMenu();
    fridgeInput.focus();
  }

  function deselect(value) {
    var i = selected.indexOf(value);
    if (i !== -1) {
      selected.splice(i, 1);
      renderChips();
      filterMenu();
      applyFilters();
    }
  }

  options.forEach(function (opt) {
    opt.addEventListener('mousedown', function (e) {
      // mousedown (not click) so we act before the input's blur closes the menu.
      e.preventDefault();
      select(opt.getAttribute('data-value'));
    });
  });

  fridgeInput.addEventListener('focus', function () { filterMenu(); openMenu(); });
  fridgeInput.addEventListener('input', function () { filterMenu(); openMenu(); });
  fridgeInput.addEventListener('keydown', function (e) {
    if (e.key === 'Backspace' && fridgeInput.value === '' && selected.length > 0) {
      deselect(selected[selected.length - 1]);
    } else if (e.key === 'Enter') {
      e.preventDefault();
      var firstVisible = options.filter(function (o) { return !o.hidden; })[0];
      if (firstVisible) select(firstVisible.getAttribute('data-value'));
    } else if (e.key === 'Escape') {
      closeMenu();
    }
  });

  // Close the menu when focus/clicks leave the fridge widget.
  document.addEventListener('click', function (e) {
    if (!fridge.contains(e.target)) closeMenu();
  });

  // Clicking the control area focuses the input.
  fridge.querySelector('.fridge-control').addEventListener('click', function () {
    fridgeInput.focus();
  });

  // Title search runs on Enter: show only recipes whose title contains the input.
  titleSearch.addEventListener('keydown', function (e) {
    if (e.key === 'Enter') {
      e.preventDefault();
      applyFilters();
    }
  });
  // Clearing the box (native "x" on a search field, or emptying it) resets the title filter.
  titleSearch.addEventListener('search', function () {
    if (titleSearch.value === '') applyFilters();
  });

  filterMenu();
  applyFilters(); // populate the "Showing X of Y" count on load
})();
