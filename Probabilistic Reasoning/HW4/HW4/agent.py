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
from classes import *

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

def agent():
    
    JTA = Clique("JTA", psi_JTA)
    MNA = Clique("MNA", psi_MNA)
    BEA = Clique("BEA", psi_BEA)

    delta_JTA_A_BEA = Delta(JTA, BEA)
    delta_MNA_A_BEA = Delta(MNA, BEA)
    delta_BEA_A_JTA = Delta(BEA, JTA, delta_JTA_A_BEA)
    delta_BEA_A_MNA = Delta(BEA, MNA, delta_MNA_A_BEA)

    cliques = [JTA, MNA, BEA]

    for i in cliques:
        print "Clique:", i
        print "Incoming Deltas:"
        print i.incoming_deltas
        print "Outgoing Deltas:"
        print i.outgoing_deltas
        print

    print "#############################"
    print "## Clique Tree Calibration ##"
    print "#############################"
    for clique in cliques:
        r = clique.ready
        outgoing_delta = None
        if r == 1:
            print clique, "is ready"
            psi = clique.psi[:]
            for delta in clique.incoming_deltas:
                if delta.ready == False:
                    outgoing_delta = delta.inverse
                else:
                    psi = [np.multiply(psi[0], delta.value.item(0)),
                            np.multiply(psi[1], delta.value.item(1))]
            print outgoing_delta
            outgoing_delta.value = marginalize_first_second(psi)
            print outgoing_delta.value
            print
        elif r == 0:
            print clique, "is ready"
            psi = clique.psi[:]
            for outgoing_delta in clique.outgoing_deltas:
                for delta in clique.incoming_deltas:
                    if delta.inverse.__str__() != outgoing_delta.__str__():
                        psi = matrixwise_multiply(psi, delta.value)
                print outgoing_delta
                outgoing_delta.value = marginalize_first_second(psi)
                print outgoing_delta.value
            print

    ###############################
    ## Compute the distributions ##
    ###############################
    
    print "##########################"
    print "## Unnormalized Beliefs ##"
    print "##########################\n"
    for clique in cliques:
        if clique.incoming_deltas_ready == len(clique.incoming_deltas):
            clique.compute_belief()
            print "Distribution for", clique
            table(clique.belief, clique.id)
            print

    print "###################"
    print "## Probabilities ##"
    print "###################\n"
    for clique in cliques:
        if clique.incoming_deltas_ready == len(clique.incoming_deltas):
            prob = clique.compute_prob()
            print "In Clique", clique
            for key in prob.keys():
                print "P("+key+")"
                print_1d(normalized(prob[key]))
                print

    #######################################
    ## Compute probability of P(E|j1,m0) ##
    #######################################
   

    print "#######################################"
    print "## Compute probability of P(E|j1,m0) ##"
    print "#######################################"

    ## Compute new psi's for clique JTA and MNA

    JTA.psi = np.concatenate((JTA.psi[0][:,1].T, JTA.psi[1][:,1].T), axis=0)
    MNA.psi = np.concatenate((MNA.psi[0][:,0].T, MNA.psi[1][:,0].T), axis=0)

    for clique in cliques[:2]:
        clique.incoming_deltas_ready = 0
        print "Outbound message from Clique", clique
        psi = clique.psi[:]
        for outgoing_delta in clique.outgoing_deltas:
            for delta in clique.incoming_deltas:
                if delta.inverse.__str__() != outgoing_delta.__str__():
                    psi = matrixwise_multiply(psi, delta.value)
            print "Delta:", outgoing_delta, "value:"
            outgoing_delta.value = marginalize_first(psi)
            print_1d(outgoing_delta.value)
        print

    BEA.compute_belief()
    prob = BEA.compute_prob()
    print "P(E|j1,m0)"
    print_1d(normalized(prob["E"]))

    
if __name__ == "__main__":
    np.set_printoptions(suppress=True, precision=8)
    print
    agent()
