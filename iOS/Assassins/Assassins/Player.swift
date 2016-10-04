//
//  Player.swift
//  Assassins
//
//  Created by Scott Rowekamp on 10/3/16.
//  Copyright © 2016 LA-05. All rights reserved.
//

import Foundation

class Player {

    var name:String
    var username:String
    var password:String
    var userPhoto:String?
    
    
    init(name: String, username: String, password:String, userPhoto:String?) {
        self.name = name
        self.username = username
        self.password = password
        if userPhoto != nil {
            self.userPhoto = userPhoto!
        }
        return
    }
}
