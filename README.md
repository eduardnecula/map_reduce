@ 2021
Necula Eduard-Ionut 332CA

Topic 2 Parallel and Distributed Algorithms

Running theme: (from src)
javac * .java
java Theme2 <workers> <in_file> <out_file>

The Main program is implemented in Theme2.java

What's happening in Main: Theme2.java

    0 - INPUT STAGE: ReadInput.java

1. Read the input received by arguments, when running the topic.

2. All files are read and calculated by how many tasks I need.
For example, if the size of all files is 41 bytes and
divide the space into X characters, 5 tasks will result, int no = 41/19 + 41% 10.
And this happens for each file.

3. I will read from the files, and for each file, I will put in the assigned tasks,
the X characters, with spaces and other characters encountered.

    I - STAGE MAP: Task.java + MapTask.java

4. Start the first part of the parallelization, the Map Stage. So many threads will start
execution, as they receive at input. Each thread receives the list of
tasks. In this stage, the correct placement of the words is solved first,
in each task. E.g:
in1.txt
Task0: [outside es]
Task1: [get beautiful]
Task2: [s]

It can be seen that some words are not properly placed in tasks. program
go through each individual task, from each file, and it will try to modify
program, both left and right. In the [outside es] sequence, the program will try
change to the left, but see that there is nothing, then change to the right.
He sees that the right side does not end in space, so he will look at the next one
task, sees that it does not start in space, so it can "steal" the first word until the first
space-bar. Task0 will become [outside is]. The only thing that changes in the task
you will be the offset, and the size / length of the number of characters read, from the offset.

5. After the correct placement of the words in each task, it will be put in
the map associated with the task, the length of the words and how many each one appears.

6. When all threads are finished, join the above operations.

    II - STAGE REDUCE: Reduce.java

7. In this second, we know every task I have, more precisely every length of speech
and the number of appearances put in a map. The reduced stage begins, in which we combine everything
spent on the map stage only as a single map, on each file separately.

8. In the Reduce.Java class, all the tasks from the Map stage will be completed.
If that task is part of its file, I will make a final map for
that file, in which I put exactly as in the MAP stage, the maximum word length, as well as
the number of his appearances. After completing the map with the information
fit, I will move on to calculating the file rank.
    The created map will be traversed and with the help of the following formula the rank will be calculated:
    float rank = [fib (maxCuvMaximum length + 1) * noAppearancesMaxMax Length] / nrCuvFile
    fib - the function of the fibonac that returns the number from the sequence from a certain position

9. After finishing the threads, the 2nd join will be given. Now every file
has calculated the rank, the maximum word length, as well as its number of occurrences.

    III - CREATING FILES

10. To sort the files, each piece of information found in the Reduce objects,
were put in WriteOutput.java objects, which implemented Comparable.

11. The objects are placed in a list, sorted in descending order by rank, and at
will be displayed in the output file received from the command line.

Remarks:
The code passes 25 - 28 points out of 30, if the sequential map stage is run using run (),
instead of starting, then the program takes 30/30. It's probably a matter of separators,
or something changes something before it is allowed to be changed. Only fail the most test
small, so on large tests the code is good, there may be another small problem with the code.

The concept of Map-Reduce is respected, because each file is divided into pieces, it is done
a map for that piece, and at the end all the maps are put on each task,
in a single map on each file, and everything is done in parallel,
on each thread.

Homework duration: 20-25 hours max, divided into 3 days, and 8-10 hours just to understand the first problem
From lower.

Problems encountered:
    The problem of understanding why the first test0.txt passes, which is the easiest,
and the rest falls. It was solved by creating other smaller tests in which
it was noticed that if I had (5, 1) and another (5, 2) he would not give me (5, 3), he would give me
(5, 2), because on the first test he was going to collect (5, 1 +1) and give (5, 2)
    Take care of the separators. If you are not careful and omit something, everything goes.
    Some characters such as "'" may be encoded differently or on windows,
or on linux. On windows with Intelijei he wanted a quotation mark character, and on linux,
another, stupid stupid problem :)).

Lifehack:
    Write the problem on paper, understand what he wants from you, giving the short example,
only then write code