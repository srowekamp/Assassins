//
//  TrackerVC.swift
//  Assassins
//
//  Created by Scott Rowekamp on 10/4/16.
//  Copyright © 2016 LA-05. All rights reserved.
//

import UIKit
import CoreLocation

class TrackerVC: UIViewController, updateable {

    func updateUI() {
        if game!.isRunning {
            let userLoc = CLLocation(latitude: currentUser!.x_location!,longitude: currentUser!.y_location!)
            let targetLoc = CLLocation(latitude: (currentTarget!.x_location)!, longitude: (currentTarget!.y_location)!)
            let bearing = CGFloat(getBearingBetweenTwoPoints(point1: userLoc, point2: targetLoc))
            
            //print(userLoc)
            //print(targetLoc)
            //print(bearing)
            
            compassImage.transform = .identity
            compassImage.transform = CGAffineTransform(rotationAngle: bearing)
        }
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
