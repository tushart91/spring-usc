#!/usr/bin/env python

########################################
# @file     agent.py
# @author   Tushar Tiwari
# @email    ttiwari@usc.edu
# @course   Probabilistic Reasoning/HW4
# @prof     Prof Ram Nevatia
# @date     2015-03-05
########################################

import numpy as np
from numpy import matrix

def pointwise_multiply(factor_3d, factor_1d):
    psi_3d0 = matrix([
                        [factor_3d[0].item(0,0) * factor_1d.item(0),
                         factor_3d[0].item(0,1) * factor_1d.item(0)],
                        [factor_3d[0].item(1,0) * factor_1d.item(1),
                         factor_3d[0].item(1,1) * factor_1d.item(1)]
                    ])

    psi_3d1 = matrix([
                        [factor_3d[1].item(0,0) * factor_1d.item(0),
                         factor_3d[1].item(0,1) * factor_1d.item(0)],
                        [factor_3d[1].item(1,0) * factor_1d.item(1),
                         factor_3d[1].item(1,1) * factor_1d.item(1)]
                    ])
    return [psi_3d0, psi_3d1]

def normalized(prob):
    A = prob.item(0)
    B = prob.item(1)
    prob.itemset(0, A / (A+B))
    prob.itemset(1, B / (A+B))
    return prob

def marginalize_last(belief):
    return belief[0] + belief[1]

def marginalize_middle(belief):
    return np.sum(belief, axis = 0)

def marginalize_first(belief):
    return np.sum(belief, axis = 1)

def marginalize_first_second(belief):
    return np.matrix([np.sum(belief[0]), np.sum(belief[1])])

def agent():

    ########################
    ## Initialize Cliques ##
    ########################
    factor_T = matrix([[0.2], [0.8]])
    factor_JTA0 = matrix([ 
                      [0.98, 0.02],
                      [0.97, 0.03] 
                  ])
    factor_JTA1 = matrix(
                  [
                      [0.12, 0.88],
                      [0.55, 0.45] 
                  ])
    factor_JTA = [factor_JTA0, factor_JTA1]
    psi_JTA = pointwise_multiply(factor_JTA, factor_T)

    factor_N = matrix([[0.75], [0.25]])
    factor_MNA0 = matrix([
                        [0.99, 0.01],
                        [0.999, 0.001]
                    ])
    factor_MNA1 = matrix([
                        [0.25, 0.75],
                        [0.75, 0.25]
                    ])
    factor_MNA = [factor_MNA0, factor_MNA1]
    psi_MNA = pointwise_multiply(factor_MNA, factor_N)

    factor_E = matrix([[0.999],[0.001]])
    factor_B = matrix([[0.998],[0.002]])
    factor_EBA0 = matrix([
                        [0.998, 0.4],
                        [0.15, 0.07]
                    ])
    factor_EBA1 = matrix([
                        [0.002, 0.6],
                        [0.85, 0.93]
                    ])
    factor_EBA = [factor_EBA0, factor_EBA1]
    psi_EBA = pointwise_multiply(factor_EBA, factor_B)
    factor_BEA = [psi_EBA[0].T, psi_EBA[1].T]
    psi_BEA = pointwise_multiply(factor_BEA, factor_E)
    print psi_JTA
    ########################
    ## Compute the deltas ##
    ########################
    delta_A_BEA_JTA = marginalize_first_second(psi_BEA)
    #print delta_A_BEA_JTA
    delta_A_BEA_MNA = marginalize_first_second(psi_BEA)
    #print delta_A_BEA_MNA
   
    delta_A_JTA_BEA = marginalize_first_second(psi_JTA)
    #print delta_A_JTA_BEA
    delta_A_MNA_BEA = marginalize_first_second(psi_MNA)
    #print delta_A_MNA_BEA
    
    ########################
    ## Compute the beiefs ##
    ########################

    belief_JTA = [np.multiply(psi_JTA[0], delta_A_BEA_JTA.item(0)),
                    np.multiply(psi_JTA[1], delta_A_BEA_JTA.item(1))]
    #print belief_JTA[0]
    #print belief_JTA[1]
    #print
    belief_MNA = [np.multiply(psi_MNA[0], delta_A_BEA_MNA.item(0)),
                    np.multiply(psi_MNA[1], delta_A_BEA_MNA.item(1))]
    #print belief_MNA[0]
    #print belief_MNA[1]
    #print
    belief_BEA = [np.multiply(np.multiply(psi_BEA[0], delta_A_JTA_BEA.item(0)),
                        delta_A_MNA_BEA.item(0)),
                    np.multiply(np.multiply(psi_BEA[1], delta_A_JTA_BEA.item(1)),
                        delta_A_MNA_BEA.item(1))]
    #print belief_BEA[0]
    #print belief_BEA[1]
    #print

    ###############################
    ## Compute the distributions ##
    ###############################
    
    probability_A2 = marginalize_first_second(belief_JTA)
    probability_JT = marginalize_last(belief_JTA)
    probability_J  = marginalize_middle(probability_JT)
    probability_T  = marginalize_first(probability_JT)
    probability_MN = marginalize_last(belief_MNA)
    probability_M  = marginalize_middle(probability_MN)
    probability_N  = marginalize_first(probability_MN)

    probability_A = marginalize_first_second(belief_BEA)
    probability_BE = marginalize_last(belief_BEA)
    probability_B  = marginalize_middle(probability_BE)
    probability_E  = marginalize_first(probability_BE)

    #######################################
    ## Compute probability of P(E|j1,m0) ##
    #######################################
   
    ## Compute new psi's for clique JTA and MNA

    print psi_JTA
    psi_JTA = np.concatenate((psi_JTA[0][:,1].T, psi_JTA[1][:,1].T), axis=0)
    psi_MNA = np.concatenate((psi_MNA[0][:,0].T, psi_MNA[1][:,0].T), axis=0)

    delta_A_JTA_BEA = marginalize_first(psi_JTA)
    delta_A_MNA_BEA = marginalize_first(psi_MNA)
    print delta_A_JTA_BEA
    print delta_A_MNA_BEA
    belief_BEA = [np.multiply(np.multiply(psi_BEA[0], delta_A_JTA_BEA.item(0)),
                        delta_A_MNA_BEA.item(0)),
                    np.multiply(np.multiply(psi_BEA[1], delta_A_JTA_BEA.item(1)),
                        delta_A_MNA_BEA.item(1))]

    probability_A = marginalize_first_second(belief_BEA)
    probability_BE = marginalize_last(belief_BEA)
    probability_B  = marginalize_middle(probability_BE)
    probability_E  = marginalize_first(probability_BE)

    #print normalized(probability_A)
    #print normalized(probability_B)
    #print normalized(probability_E)

if __name__ == "__main__":
    np.set_printoptions(suppress=True, precision=8)
    agent()
