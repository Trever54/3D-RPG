package collisionDetection;

import org.lwjgl.util.vector.Vector3f;

import toolbox.Maths;

public class CollisionPacket {

	// ellipsoid radius
	public Vector3f eRadius;
	
	// Information about the move being requested: (in R3)
	public Vector3f R3Velocity;
	public Vector3f R3Position;
	
	// Information about the move being requested: (in eSpace)
	public Vector3f velocity;
	public Vector3f normalizedVelocity;
	public Vector3f basePoint;
	
	// Hit Information
	public boolean foundCollision;			// not initialized in appendix
	public double nearestDistance;			// not initialized in appendix
	public Vector3f intersectionPoint;		// not initialized in appendix
	
	// newT float variable (for checkLowestRoot and checkTriangle methods)
	public float newT;
	
	public CollisionPacket(){
		
		// Set the following variables
		/* SET THE FOLLOWING VARIABLES
		 *(NOT USED IN CODE/USED TO FIGURE OUT OTHERS)
		 *	-eRadius
		 *	-R3Velocity
		 * 	-R3Position
		 * 
		 *(Are called upon in code. Use other variables to calculate them)
		 * 	-velocity
		 * 	-normalizedVelocity
		 * 	-basePoint
		 * 
		 *(FIGURE OUT IF THESE NEED TO BE INITIALIZED?)
		 * 	-foundCollision
		 * 	-nearestDistance
		 * 	-intersectionPoint
		 * 
		 */
		
		
	}

	// Function that will check a single triangle for collision
	// Assumes: p1, p2, and p3 are given in ellipsoid space
	public void checkTriangle(CollisionPacket colPackage, Vector3f p1, Vector3f p2, Vector3f p3){
		Plane trianglePlane = new Plane(p1,p2,p3);	// Make the plane containing this triangle
		
		// Is triangle front-facing to the velocity vector?
		// We only check front-facing triangles
		if(trianglePlane.isFrontFacingTo(colPackage.normalizedVelocity)){
			// Get interval of plane intersection
			double t0, t1;
			boolean embeddedInPlane = false;
			
			// Calculate the signed distance from sphere and position to triangle plane
			double signedDistToTrianglePlane = trianglePlane.signedDistanceTo(colPackage.basePoint);
			
			// cache this as we're going to use it a few times below
			float normalDotVelocity = Maths.dot(trianglePlane.normal, colPackage.velocity);
			
			// If sphere is travelling parallel to the plane:
			if(normalDotVelocity == 0.0f){
				if(Math.abs(signedDistToTrianglePlane) >= 1.0f){	// ********************
					// Sphere is not embedded in plane, No collision possible
					return;
				} else {
					// Sphere is embedded in Plane. It interesects in the whole range [0..1]
					embeddedInPlane = true;
					t0 = 0.0;
					t1 = 1.0;
				}
			} else {
				// N dot D is not 0. Calculate intersection interval:
				t0 = (-1.0-signedDistToTrianglePlane)/normalDotVelocity;
				t1 = (1.0-signedDistToTrianglePlane)/normalDotVelocity;
				
				// Swap so t0 < t1
				if(t0 > t1) {
					double temp = t1;
					t1 = 10;
					t0 = temp;
				}
				
				// Check that at least one result is within range:
				if(t0 > 1.0f || t1 < 0.0f){
					// Both t values are outside values [0,1]. No collision possible
					return;
				}
				
				// Clamp to [0,1]
				if(t0 < 0.0) t0 = 0.0;
				if(t1 < 0.0) t1 = 0.0;
				if(t0 > 1.0) t0 = 1.0;
				if(t1 > 1.0) t1 = 1.0;			
			}
			
			// If any collisions occur, it must be within this interval
			Vector3f collisionPoint = new Vector3f();
			boolean foundCollision = false;
			float t = 1.0f;
			
			// First check for the easy case - Collision inside the triangle
			if(!embeddedInPlane) {
				Vector3f planeIntersectionPoint = Maths.add((Maths.sub(colPackage.basePoint, trianglePlane.normal)), (Maths.scale(t0, colPackage.velocity)));
				
				if(checkPointInTriangle(planeIntersectionPoint, p1, p2, p3)){
					foundCollision = true;
					t = (float) t0;
					collisionPoint = planeIntersectionPoint;
				}
			} // End if(!embeddedInPlane)
			
			// If no collision is found yet, we have to sweep sphere against points and edges of the triangle
			if(foundCollision == false){
				// Some commonly used terms:
				Vector3f velocity = colPackage.velocity;
				Vector3f base = colPackage.basePoint;
				float velocitySquaredLength = Maths.getSquaredLength(velocity);
				float a, b, c; // Params for equation
				
				// For each vertex or edge a quadratic equations has to be solved
				// Check against points
				a = velocitySquaredLength;
				
				// P1
				b = (float) (2.0*(Maths.dot(velocity, Maths.sub(base, p1))));
				c = (float) (Maths.getSquaredLength(Maths.sub(p1,base)) - 1.0);		
				if(getLowestRoot(a,b,c,t)){
					t = newT;
					foundCollision = true;
					collisionPoint = p1;
				}
				
				// P2
				b = (float) (2.0*(Maths.dot(velocity, Maths.sub(base, p2))));
				c = (float) (Maths.getSquaredLength(Maths.sub(p2,base)) - 1.0);		
				if(getLowestRoot(a,b,c,t)){
					t = newT;
					foundCollision = true;
					collisionPoint = p2;
				}
				
				// P3
				b = (float) (2.0*(Maths.dot(velocity, Maths.sub(base, p3))));
				c = (float) (Maths.getSquaredLength(Maths.sub(p3,base)) - 1.0);		
				if(getLowestRoot(a,b,c,t)){
					t = newT;
					foundCollision = true;
					collisionPoint = p3;
				}
				
				// Check against edges:
				
				// p1 -> p2:
				Vector3f edge = Maths.sub(p2, p1);
				Vector3f baseToVertex = Maths.sub(p1, base);
				float edgeSquaredLength = Maths.getSquaredLength(edge);
				float edgeDotVelocity = Maths.dot(edge, velocity);
				float edgeDotBaseToVertex = Maths.dot(edge, baseToVertex);
				
				// Calculate parameters for equation
				a = edgeSquaredLength*(-velocitySquaredLength) + edgeDotVelocity*edgeDotVelocity;
				b = (float) (edgeSquaredLength*(2*(Maths.dot(velocity, baseToVertex))) - 2.0*edgeDotVelocity*edgeDotBaseToVertex);
				c = edgeSquaredLength*(1-(Maths.getSquaredLength(baseToVertex))) + edgeDotBaseToVertex*edgeDotBaseToVertex;
				
				// Does the swept sphere collisde against infinite edge?
				if(getLowestRoot(a,b,c,t)){
					// check is intersection is within line segment:
					float f = (edgeDotVelocity*newT - edgeDotBaseToVertex)/edgeSquaredLength;
					
					if(f >= 0.0 && f <= 1.0){
						// intersection took place within segment
						t = newT;
						foundCollision = true;
						collisionPoint = Maths.add(p1, Maths.scale(f, edge));
					}
				}
				
				// p2 -> p3:
				edge = Maths.sub(p3, p2);
				baseToVertex = Maths.sub(p2, base);
				edgeSquaredLength = Maths.getSquaredLength(edge);
				edgeDotVelocity = Maths.dot(edge, velocity);
				edgeDotBaseToVertex = Maths.dot(edge, baseToVertex);
				
				a = edgeSquaredLength*(-velocitySquaredLength) + edgeDotVelocity*edgeDotVelocity;
				b = (float) (edgeSquaredLength*(2*(Maths.dot(velocity, baseToVertex))) - 2.0*edgeDotVelocity*edgeDotBaseToVertex);
				c = edgeSquaredLength*(1-(Maths.getSquaredLength(baseToVertex))) + edgeDotBaseToVertex*edgeDotBaseToVertex;
				
				if(getLowestRoot(a,b,c,t)){
					float f = (edgeDotVelocity*newT - edgeDotBaseToVertex)/edgeSquaredLength;
					
					if(f >= 0.0 && f <= 1.0){
						t = newT;
						foundCollision = true;
						collisionPoint = Maths.add(p2, Maths.scale(f, edge));
					}
				}
				
				// p3 -> p1:
				edge = Maths.sub(p3, p1);
				baseToVertex = Maths.sub(p3, base);
				edgeSquaredLength =Maths.getSquaredLength(edge);
				edgeDotVelocity = Maths.dot(edge, velocity);
				edgeDotBaseToVertex = Maths.dot(edge, baseToVertex);
				
				a = edgeSquaredLength*(-velocitySquaredLength) + edgeDotVelocity*edgeDotVelocity;
				b = (float) (edgeSquaredLength*(2*(Maths.dot(velocity, baseToVertex))) - 2.0*edgeDotVelocity*edgeDotBaseToVertex);
				c = edgeSquaredLength*(1-(Maths.getSquaredLength(baseToVertex))) + edgeDotBaseToVertex*edgeDotBaseToVertex;
				
				if(getLowestRoot(a,b,c,t)){
					float f = (edgeDotVelocity*newT - edgeDotBaseToVertex)/edgeSquaredLength;
					
					if(f >= 0.0 && f <= 1.0){
						t = newT;
						foundCollision = true;
						collisionPoint = Maths.add(p3, Maths.scale(f, edge));
					}
				}		
			} // End if(foundCollision == false)
			
			// Set result:
			if(foundCollision == true){
				// distance to collision: t is time of collision
				float distToCollision = t*colPackage.velocity.length();
				
				// Does this triangle qualify for the closest hit? It does if it's the first hit or the closest
				if(colPackage.foundCollision == false || distToCollision < colPackage.nearestDistance){
					// Collision information necessary for sliding
					colPackage.nearestDistance = distToCollision;
					colPackage.intersectionPoint = collisionPoint;
					colPackage.foundCollision = true;				
				}		
			} // End if(foundCollision == true)		
		}// End if(trianglePane.isFrontFacingTo			
	}	
	
	// APPENDIX D
	// The following solves a quadratic equation and returns the lowest root below a certain threshold (the maxR parameter)
	// newT replaces root in my correction (for Java) as it simplifies one less variable to pass. Should work just the same.
	public boolean getLowestRoot(float a, float b, float c, float maxR){
		// Check to see if a solution exists
		float determinant = b*b - 4.0f*a*c;
		
		// If determinant is negative it means no solutions
		if(determinant < 0.0f) return false;
		
		// calculate the two roots: (if determinant == 0 then x1 == x2 but let's disregard that slight optimization)
		float sqrtD = (float) Math.sqrt(determinant);
		float r1 = (-b - sqrtD) / (2*a);
		float r2 = (-b - sqrtD) / (2*a);
		
		// Sort so x1 <= x2
		if(r1 > r2) {
			float temp = r2;
			r2 = r1;
			r1 = temp;
		}
		
		// Get Lowest Root
		if (r1 > 0 && r1 < maxR){
			newT = r1;
			return true;
		}
		
		// It is possible that we want x2 - this can happen if x1 < 0
		if(r2 > 0 && r2 < maxR){
			newT = r2;
			return true;
		}
		
		// No (valid) solutions
		return false;	
	}
	
	// APPENDIX C
	// The following determines if a point is inside a triangle or not (used in collision detection step)
	public boolean checkPointInTriangle(Vector3f point, Vector3f pa, Vector3f pb, Vector3f pc){
		Vector3f e10 = new Vector3f(Maths.sub(pb, pa));
		Vector3f e20 = new Vector3f(Maths.sub(pc, pa));
		
		float a = Maths.dot(e10, e10);
		float b = Maths.dot(e10, e20);
		float c = Maths.dot(e20, e20);
		float ac_bb = (a*c)-(b*b);
		Vector3f vp = new Vector3f(point.x-pa.x, point.y-pa.y, point.z-pa.z);
		
		float d = Maths.dot(vp, e10);
		float e = Maths.dot(vp, e20);
		float x = (d*c)-(e*b);
		float y = (e*a)-(d*b);
		float z = x+y-ac_bb;
		
		return true;	// Return?? FIGURE THIS OUT LATER ***		
	}
	
	
} // End Class
