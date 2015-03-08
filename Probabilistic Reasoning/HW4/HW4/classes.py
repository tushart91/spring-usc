#!/usr/bin/env python

########################################
# @file     class.py
# @author   Tushar Tiwari
# @email    ttiwari@usc.edu
# @course   Probabilistic Reasoning/HW4/HW4
# @prof     
# @date     2015-03-07
########################################

import numpy as np
from numpy import matrix

def print_3d(item, string):
    print "#########################################################################"
    print("#\t"+string[-1]+"\t|\t"+string[-2]+"\t|\t"+string[-3]+"\t|\t"+\
            "      P\t\t#")
    print "#########################################################################"

    last   = False
    middle = False
    first  = False
    for i in range(2):
        for j in range(2):
            for k in range(2):
                print("|\t"+str(last)+"\t|\t"+str(middle)+"\t|\t"+str(first)\
                        +"\t|\t"+str('%f' % round(item[i].item(j,k),8))+"\t|")
                first = not first
            middle = not middle
        last = not last
    print "#########################################################################"

def print_2d(item, string):
    print "#########################################################"
    print("#\t"+string[-1]+"\t|\t"+string[-2]+"\t|\t      P\t\t#")
    print "#########################################################"

    last   = False
    middle = False
    for i in range(2):
        for j in range(2):
            print("|\t"+str(last)+"\t|\t"+str(middle)+"\t|\t"+\
                    str('%f' % round(item[i].item(j),8))+"\t|")
            middle = not middle
        last = not last
    print "#########################################################"

def print_1d(item):
    print "False", "=", round(item.item(0), 8)
    print "True", " =", round(item.item(1), 8)

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

def matrixwise_multiply(belief, value):
    return [np.multiply(belief[0], value.item(0)),
            np.multiply(belief[1], value.item(1))]

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

class Delta(object):
    def __init__(self, _from_, _to_, inverse = None):
        self._from_  = _from_
        self._to_    = _to_
        self.scope   = self._scope()
        _from_.outgoing_deltas.append(self)
        _to_.incoming_deltas.append(self)
        self._value   = None
        self.ready   = False
        self.inverse = inverse
        if inverse != None:
            self.inverse.inverse = self

    @property
    def value(self):
        return self._value
       
    @value.setter
    def value(self, x):
        self._to_.incoming_deltas_ready = 1 + self._to_.incoming_deltas_ready
        self.ready = True
        self._value = x

    def _scope(self):
        scope = ""
        for i in self._from_.id:
            if i in self._to_.id:
                scope += i
        return scope

    def __str__(self):
        return self._from_.id + " -> " + self._to_.id + "(" + self.scope + ")"
    def __repr__(self):
        return self.__str__()

class Clique:
    def __init__(self, id, psi):
        self.id = id
        self.psi = psi
        self.outgoing_deltas = []
        self.incoming_deltas = []
        self.incoming_deltas_ready = 0
        self.belief = None

    def compute_belief(self):
        self.belief = self.psi[:]
        for i in self.incoming_deltas:
            self.belief = matrixwise_multiply(self.belief, i.value) 

    def compute_prob(self, evidence=False):
        ret = {}
        if not evidence:
            ret[self.id[2]] = normalized(marginalize_first_second(self.belief))
            probability_12 = marginalize_last(self.belief)
            ret[self.id[0]] = normalized(marginalize_middle(probability_12))
            ret[self.id[1]] = normalized(marginalize_first(probability_12))
        else:
            ret[self.id[2]] = normalized(marginalize_first_second(self.belief))
            ret[self.id[1]] = normalized(marginalize_last(self.belief))
        return ret

    @property
    def ready(self):
        r = len(self.outgoing_deltas) - self.incoming_deltas_ready
        if   r == 0: return 0
        elif r == 1: return 1
        return -1

    def __eq__(self, clique):
        if clique.id == self.id:
            return True
        return False

    def __neq__(self, clique):
        if clique.id != self.id:
            return True
        return False

    def __str__(self):
        return self.id
    def __repr__(self):
        return self.id
    
