//
//  Player.swift
//  Assassins
//
//  Created by Scott Rowekamp on 10/3/16.
//  Copyright © 2016 LA-05. All rights reserved.
//

import Foundation

class Player {

    var id:Int
    var username:String
    var password:String
    var real_name:String
    var image_filename:String
    
    var games_played:Int
    var total_kills:Int
    
    var x_location:Int
    var y_location:Int
    
    init(id:Int, username:String, password:String, real_Name:String, image_filename:String, games_played:Int, total_kills:Int, x_location:Int, y_location:Int){
        self.id = id
        self.username = username
        self.password = password
        self.real_name = real_Name
        self.image_filename = image_filename
        self.games_played = games_played
        self.total_kills = total_kills
        self.x_location = x_location
        self.y_location = y_location
    }
}
