//
//  TrackerVC.swift
//  Assassins
//
//  Created by Scott Rowekamp on 10/4/16.
//  Copyright © 2016 LA-05. All rights reserved.
//

import UIKit
import CoreLocation
import Alamofire
import SwiftyJSON

class TrackerVC: UIViewController, updateable {
    
    let KILL_URL = "http://proj-309-la-05.cs.iastate.edu:8080/Assassins/Kill"
    
    func updateUI() {
        if game!.isRunning {
            let userLoc = CLLocation(latitude: currentUser!.x_location!,longitude: currentUser!.y_location!)
            let targetLoc = CLLocation(latitude: (currentTarget!.x_location)!, longitude: (currentTarget!.y_location)!)
            let bearing = CGFloat(getBearingBetweenTwoPoints(point1: userLoc, point2: targetLoc))
            
            compassImage.transform = .identity
            compassImage.transform = CGAffineTransform(rotationAngle: bearing)
            currentTargetLabel.text?.removeAll()
            currentTargetLabel.text = "Current Target: \(currentTarget!.real_name)"
            let distance = userLoc.distance(from: targetLoc)
            print(distance)
            print(distance < 100.0)
            if (distance < 100.0) {
                killButton.isEnabled = true
            } else {
                killButton.isEnabled = false
            }
        }
    }
    
    @IBOutlet weak var killButton: UIButton!
    
    @IBOutlet weak var currentTargetLabel: UILabel!
    
    @IBAction func killTarget(_ sender: Any) {
        var paramaters = [String:String]()
        paramaters["gameid"] = "\(game!.gameID)"
        paramaters["id"] = "\(currentUser!.id)"
        
        Alamofire.request(KILL_URL, parameters: paramaters).responseJSON { response in
            if response.result.isSuccess {
                let returned_data = JSON(response.result.value!)
                
                if returned_data["result"].string! == "success" {
                    print("user killed")
                }
                self.popUpAlert(title: "Login Faled:", message: "The username or password is incorrect, please try again.", handler: nil)
            } else {
                self.popUpAlert(title: "Server Call Failed", message: "Please check your network connection and verify you are using the IOWA STATE VPN", handler: nil)
            }
        }
        
        
        // kill target
    }
    
    @IBOutlet weak var compassImage: UIImageView!

    override func viewDidLoad() {
        super.viewDidLoad()
        game?.viewsToUpdate.append(self)
    }
    
    func XXRadiansToDegrees(radians: Double) -> Double {
        return radians * 180.0 / M_PI
    }
    
    func getBearingBetweenTwoPoints(point1 : CLLocation, point2 : CLLocation) -> Double {
        // Returns a float with the angle between the two points
        let x = point1.coordinate.longitude - point2.coordinate.longitude
        let y = point1.coordinate.latitude - point2.coordinate.latitude
        
        return fmod((radians: atan2(y, x)), 360.0) + 90.0
    }
}
