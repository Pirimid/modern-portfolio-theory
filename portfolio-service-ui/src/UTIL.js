export function arrayUnion(arr1, arr2, equalityFunc) {
  var union = arr1.concat(arr2);

  for (var i = 0; i < union.length; i++) {
      for (var j = i+1; j < union.length; j++) {
          if (equalityFunc(union[i], union[j])) {
              union.splice(j, 1);
              j--;
          }
      }
  }

  return union;
}