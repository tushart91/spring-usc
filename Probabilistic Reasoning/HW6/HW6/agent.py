#!/usr/bin/env python
# -*- coding: utf-8 -*-
########################################
# @file     agent.py
# @author   Tushar Tiwari
# @email    ttiwari@usc.edu
# @course   Probabilistic Reasoning/HW6
# @prof     Prof Ram Nevatia
# @date     2015-04-03
########################################

from classes import *

def agent():

    # Initialize probability values
    B = [0.998, 0.002]
    E = [0.999, 0.001]

    # a0    e0     e1
    A = [[[0.998, 0.4],
          [0.15, 0.07]],
    # a1    e0     e1
         [[0.002, 0.6],
          [0.85, 0.93]]]
    # j1   a0    a1
    J = [[0.02, 0.88],   #t0
          [0.03, 0.45]]  #t1

    # m0   a0    a1
    M = [[0.99, 0.25],   #n0
          [0.999, 0.75]] #n1

    T = [0.2, 0.8]
    N = [0.75, 0.25]

    # T = 0
    # initial random assignment B = b0
    # P(E|b0,j1,m0) = ø(E).ø(A|B,E).ø(j1|T,A).ø(T)
    phiJTA = multiply_rowwise(J, T)
    phiMNA = multiply_rowwise(M, N)
    print phiJTA
    print phiMNA

def sum_rowwise(twoD):
    phi

def multiply_rowwise(twoD, oneD):

    ret = []
    for i in range(len(twoD)):
        ret.append([])
        for j in range(len(twoD[i])):
            ret[i].append(twoD[i][j]*oneD[i])
    return ret

if __name__ == "__main__":
    agent()
