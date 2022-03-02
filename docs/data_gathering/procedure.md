# Procedure

Code - @:source(main)

1. A get request is going to get us some html with the raw text in hurdat format
1. Drop the whitespace and html tags at the start and end of the file
1. Identify lines, in which the first two characters are letters ... these are the "header" lines of a system
1. Add a marker to each line, identifying the set of lines which are part of that hurriance
1. Group the lines by the identifier in the previous step
1. Assign the header / track to the data model, by writing functions which respect the formatting documnent
1. Write to JSON
