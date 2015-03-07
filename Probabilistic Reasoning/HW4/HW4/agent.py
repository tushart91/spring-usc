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

def agent():
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
    print psi_BEA[0]
    print psi_BEA[1]

if __name__ == "__main__":
    np.set_printoptions(suppress=True)
    agent()
