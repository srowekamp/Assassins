//
//  Game.swift
//  Assassins
//
//  Created by Scott Rowekamp on 11/6/16.
//  Copyright © 2016 LA-05. All rights reserved.
//

import Foundation

class Game {
    
    let serverID:Int
    let gameID:String
    let password:String
    let xcenter:Double
    let ycenter:Double
    let radius:Int
    let hostID:Int
    let duration:Int

    init(gameID:String, password:String, xcenter:Double, ycenter:Double, radius:Int, hostID:Int, duration:Int, serverID:Int){
        self.serverID = serverID
        self.gameID = gameID
        self.password = password
        self.xcenter = xcenter
        self.ycenter = ycenter
        self.radius = radius
        self.hostID = hostID
        self.duration = duration
    }
}
