new Grid({ 
  columns: ['Name', 'Email'],
  data: [
    ['John', 'john@example.com'],
    ['Mike', 'mike@gmail.com']
  ] 
}).render(document.getElementById('table'));