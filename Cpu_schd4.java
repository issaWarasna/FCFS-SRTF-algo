/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.cpu_schd4;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */ 

    // FCFS & SRTF
/**
 *
 * @author Jessuse
 */
import java.util.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;





class PCB {  //create the Process Control Block (PCB) for each process with the necessary data structure.
    int pid;
    int arrivalTime;
    int cpuBurst;
    int remainingTime;
    int startTime;
    int finishTime;
    int waitingTime;
    int turnaroundTime;
    int responseTime;
    boolean executed; 

    PCB(int _pid, int _arrival_time, int _cpu_burst) {
        pid = _pid;
        arrivalTime = _arrival_time;
        cpuBurst = _cpu_burst;
        remainingTime = cpuBurst;
        executed = false;
    }
}

class info { // For gantt chart
    int pid;
    int start_time;
    int end_time;

    info(int _pid, int _start_time, int _end_time) {
        pid = _pid;
        start_time = _start_time;
        end_time = _end_time;
    }
}


public class Cpu_schd4 {

    public static void main(String[] args) {
        
        List<Integer> arrivalTimes = new ArrayList<>();//initialize arrayList for arrivale time
        List<Integer> burstTimes = new ArrayList<>();//initialize arrayList for burst time
        int contextSwitch = 0;//initialize context switch
        
        int n = 0; // Number of processes
        //to read the specfic data from the file :
        String filename = "C:/data/cpu.txt"; // Specify the file name
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(" ");
                arrivalTimes.add(Integer.parseInt(values[0]));//store in the array list the value of arrivale time which stored in file
                burstTimes.add(Integer.parseInt(values[1]));//store in the array list the value of burst time which stored in file
                contextSwitch = Integer.parseInt(values[2]);//store in the array list the value of context switch which stored in file
                n++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        //creates a list of Process objects based on the arrival times and burst times 
        // with a unique PID starting from 1.
        List<PCB> processes = new ArrayList<>();//initiate the array list process from Process
        //*
        for (int i = 0; i < arrivalTimes.size(); i++) {
            Collections.sort(processes, (p1, p2) -> p1.arrivalTime - p2.arrivalTime);
                
            processes.add(new PCB(i + 1, arrivalTimes.get(i), burstTimes.get(i)));
        }        Collections.sort(processes, (p1, p2) -> p1.arrivalTime - p2.arrivalTime);

        System.out.println(processes.get(0).cpuBurst);
        //for test and show the arrivale time &burst time ...
        System.out.println("the arrivale time :"+arrivalTimes);
        System.out.println("the burst time :"+burstTimes);
        System.out.println("the context switch :"+contextSwitch);
        
        
        // The first algo is First-Come First-Served (FCFS):
        int currentTime = 0;
        List<info> FCFS_algo = new ArrayList<>();//creat an array list from info class
        for (PCB process : processes) {
            if (process.arrivalTime > currentTime) {
                currentTime = process.arrivalTime;
            }
            process.startTime = currentTime ;
            currentTime += process.cpuBurst + contextSwitch;
            process.finishTime = currentTime -contextSwitch;
            process.waitingTime = process.startTime - process.arrivalTime;// WT=ST - AT
            process.turnaroundTime = process.finishTime - process.arrivalTime;//TAT=FT-AT
            FCFS_algo.add(new info(process.pid, process.startTime, process.finishTime));
        }
        double FCFS_AWT = processes.stream().mapToInt(p -> p.waitingTime).average().orElse(0);
        double FCFS_ATAT = processes.stream().mapToInt(p -> p.turnaroundTime).average().orElse(0);
        double FCFS_CPU_UR = (double) processes.stream().mapToInt(p -> p.cpuBurst).sum() / currentTime;
        System.out.println();
        System.out.println();   
        System.out.println("*************************************************************************************************");
        System.out.println();
        System.out.println();
        
        // To print the result of fcfs algo :
        System.out.println("First Come First Serve (FCFS) (preemptive)");
        System.out.println("");
        System.out.println("Gantt Chart : ");
        // print_gantt_chart(fcfs_gantt_chart):
        System.out.println();
        printGanttChart(FCFS_algo);
        System.out.println();
        System.out.println();

        System.out.println("Start Time\tFinish Time\tWaiting Time\tTurnaround Time");
        for (PCB process : processes) {
            System.out.println(process.startTime + "\t\t"+process.finishTime + "\t\t" + process.waitingTime + "\t\t" + process.turnaroundTime);
        }        System.out.println("");

        System.out.println("Average Waiting Time = " + FCFS_AWT);
        System.out.println("Average Turnaround Time = " + FCFS_ATAT);
        System.out.println("CPU Utilization = " + FCFS_CPU_UR*100 +"%");
        System.out.println();
        System.out.println();

        System.out.println();
        System.out.println();   
        System.out.println("*************************************************************************************************");
        System.out.println();
        System.out.println();
        
        
        
        
        System.out.println("*************************************************************************************************");
         System.out.println();
        System.out.println("Shortest Job First (SRTF) (non-preemptive):");

        System.out.println();
        
        // Initialize arrays
        int[] processId = new int[n];
        int[] arrivalTime = new int[n];
        int[] burstTime = new int[n];
        int[] remainingTime = new int[n];
        int[] completionTime = new int[n];
        int[] turnaroundTime = new int[n];
        int[] waitingTime = new int[n];
        boolean[] isCompleted = new boolean[n];
        currentTime = 0;
        
       
        double totalBurst=0;
        double cpurate=0;
        int currentProcess = -1; // Initialize the current running process
        int currentStartTime = -1;
        
        
        for (int i = 0; i < arrivalTimes.size(); i++) {
            processId[i] = i + 1;  // Assign process IDs sequentially
             arrivalTime[i] = arrivalTimes.get(i);
            burstTime[i] = burstTimes.get(i);
            remainingTime[i] = burstTime[i];
        }
         List<info> SRTF_algo = new ArrayList<>(); // Gantt chart to store process information
         int completed = 0;
        while (completed != n) {
            int shortest = -1;
            for (int i = 0; i < n; i++) {
                if (!isCompleted[i] && arrivalTime[i] <= currentTime) {
                    if (shortest == -1 || remainingTime[i] < remainingTime[shortest]) {
                        shortest = i;
                    }
                }
            }

            if (shortest == -1) {
                currentTime++;
                continue;
            }

            remainingTime[shortest]--;
            
             if (currentProcess != shortest) {
                 // If the current running process changes, add the previous process to the Gantt chart
                if (currentProcess != -1) {
                    SRTF_algo.add(new info(processId[currentProcess], currentTime, currentTime + 1));
                }
                  // Update the current running process and start time
                currentProcess = shortest;
                currentStartTime = currentTime;
            }
            
            
            if (remainingTime[shortest] == 0) {
                completionTime[shortest] = currentTime + 1;
                turnaroundTime[shortest] = completionTime[shortest] - arrivalTime[shortest];
                waitingTime[shortest] = turnaroundTime[shortest] - burstTime[shortest];
                isCompleted[shortest] = true;
                completed++;
                totalBurst+=burstTime[shortest];
            }

            currentTime++;
        }
         if (currentProcess != -1) {
    SRTF_algo.add(new info(processId[currentProcess], currentStartTime, currentTime));
    }
        System.out.println("");
        System.out.println("");
        // Printing Gantt chart
        
        printGanttChart(SRTF_algo);
                System.out.println("");
        System.out.println("");
        
        System.out.println("\nProcess\tArrival Time\tBurst Time\tCompletion Time\tTurnaround Time\tWaiting Time");
        for (int i = 0; i < n; i++) {
            System.out.println(processId[i] + "\t\t" + arrivalTime[i] + "\t\t" + burstTime[i] +  "\t\t" + completionTime[i] + "\t\t" + turnaroundTime[i] + "\t\t" + waitingTime[i]);
        }

        double totalTurnaroundTime = 0;
        double totalWaitingTime = 0;
        for (int i = 0; i < n; i++) {
            totalTurnaroundTime += turnaroundTime[i];
            totalWaitingTime += waitingTime[i];
        }
        System.out.println("\nAverage Turnaround Time: " + (totalTurnaroundTime / n));
        System.out.println("Average Waiting Time: " + (totalWaitingTime / n));        
        System.out.println("TotalBurst:"+totalBurst);
        cpurate=(totalBurst/currentTime)*100;
        System.out.println("cpurate:"+cpurate+'%');
        
  
        System.out.println();
        System.out.println();   
        System.out.println("*************************************************************************************************");
        System.out.println();
        
    
        
        System.out.println();
        System.out.println();   
        System.out.println("*************************************************************************************************");
        System.out.println();
        System.out.println();
        
        
        
    }
    
       
        
     public static void printGanttChart(List<info> ganttChart) {
     int prevEndTime = 0;
    System.out.print("|");
    for (info entry : ganttChart) {
        //System.out.print(" c  |");
        System.out.printf("    P" + entry.pid + "     |"); // Adjusted formatting for better alignment
        System.out.print(" c  |");
        prevEndTime = entry.end_time;
    }
    System.out.println();

    }
     
     

}
