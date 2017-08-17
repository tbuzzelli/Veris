import java.util.*;

public class ladders {

  // Delta arrays. Given a direction, tells how many spaces
  // to move in length and width.
  static int[] dLen = {0, 1, 0, -1};
  static int[] dWid = {1, 0, -1, 0};

  static int length, width, height;
  public static void main(String[] args) {
    Scanner in = new Scanner(System.in);

    int numCases = in.nextInt();
    for(int map = 1; map <= numCases; ++map) {
      length = in.nextInt();
      width = in.nextInt();
      height = in.nextInt();

      Location start = null;
      Location end = null;

      char[][][] world = new char[height][length][];
      for(int h = height-1; h >= 0; --h) {
        for(int l = 0; l < length; ++l) {
          world[h][l] = in.next().toCharArray();

          for(int w = 0; w < width; ++w) {
            // Found the startpoint. Save for later.
            if(world[h][l][w] == 'S') {
              // System.out.println("Found start");
              start = new Location(l, w, h);
              world[h][l][w] = '.';
            }

            // Found the end point. Save for later.
            if(world[h][l][w] == 'E') {
              // System.out.println("Found end");
              end = new Location(l, w, h);
              world[h][l][w] = '.';
            }
          }
        }
      }

      // Array to determine if we have seen a given location.
      boolean[][][] seen = new boolean[height][length][width];
      seen[start.height][start.length][start.width] = true;

      // Queue to hold all of the next locations to try moving from.
      ArrayDeque<Location> q = new ArrayDeque<Location>();
      q.add(start);

      // BFS until we searched all reachable nodes or we found the end point.
      while(!q.isEmpty()) {
        Location at = q.poll();

        // If we have reached the end, stop searching.
        if(end.equals(at)) {
          break;
        }

        // If we are at a chute, follow it down until we are either
        // at the bottom of the world or found that the next location
        // is not part of the chute.
        if(world[at.height][at.length][at.width] == '*') {
          int down = 0;

          // Check if the next location is valid within the map and
          // if it is still a chute.
          while(isValid(at.length, at.width, at.height - down - 1) &&
            world[at.height - down - 1][at.length][at.width] == '*') {

            // Set the previous location to visited and increment the
            // number of times we have moved down.
            seen[at.height - down][at.length][at.width] = true;
            ++down;
          }

          // Update our location
          at.height -= down;

          // If we have moved down and we have already visited this location,
          // no point in trying to explore it again.
          if(down != 0 && seen[at.height][at.length][at.width]) {
            continue;
          }
          seen[at.height][at.length][at.width] = true;
        }

        // Test moving in each cardinal direction.
        for(int dir = 0; dir < dLen.length; ++dir) {
          int nLen = at.length + dLen[dir];
          int nWid = at.width + dWid[dir];

          // If the new location is valid and we haven't been there,
          // try going there.
          if(isValid(nLen, nWid, at.height) && !seen[at.height][nLen][nWid]) {
            seen[at.height][nLen][nWid] = true;
            q.add(new Location(nLen, nWid, at.height));
          }
        }

        // We are at a ladder. Try going up and down the ladder and getting off
        // at each location.
        if(world[at.height][at.length][at.width] == '#') {

          // Try going up the ladder
          for(int up = at.height + 1; up < height; ++up) {
            // If we are no longer on the ladder, stop moving up.
            if(world[up][at.length][at.width] != '#') {
              break;
            }

            // If we have already been here, no point in going again.
            if(seen[up][at.length][at.width]) {
              break;
            }
            seen[up][at.length][at.width] = true;

            // Test getting off the ladder in each direction
            for(int dir = 0; dir < dLen.length; ++dir) {
              int nLen = at.length + dLen[dir];
              int nWid = at.width + dWid[dir];
              // If the new location is valid and we haven't been there,
              // try going there.
              if(isValid(nLen, nWid, up) && !seen[up][nLen][nWid]) {
                seen[up][nLen][nWid] = true;
                q.add(new Location(nLen, nWid, up));
              }
            }
          }

          // Try going down the ladder
          for(int down = at.height - 1; down >= 0; --down) {
            // If we are no longer on the ladder, stop moving down.
            if(world[down][at.length][at.width] != '#') {
              break;
            }

            // If we have already been here, no point in going again.
            if(seen[down][at.length][at.width]) {
              break;
            }
            seen[down][at.length][at.width] = true;

            // Test getting off the ladder in each direction
            for(int dir = 0; dir < dLen.length; ++dir) {
              int nLen = at.length + dLen[dir];
              int nWid = at.width + dWid[dir];
              // If the new location is valid and we haven't been there,
              // try going there.
              if(isValid(nLen, nWid, down) && !seen[down][nLen][nWid]) {
                seen[down][nLen][nWid] = true;
                q.add(new Location(nLen, nWid, down));
              }
            }
          }
        }
      }

      System.out.printf("Map #%d: ", map);
      if(seen[end.height][end.length][end.width]) {
        System.out.println("Yes");
      } else {
        System.out.println("No");
      }
    }

    in.close();
  }

  // Is the given location within bounds of the world?
  static boolean isValid(int l, int w, int h) {
    return l >= 0 && l < length &&
           w >= 0 && w < width &&
           h >= 0 && h < height;
  }

  static class Location {
    int height, length, width;

    Location(int l, int w, int h) {
      height = h;
      length = l;
      width = w;
    }

    boolean equals(Location loc) {
      return height == loc.height &&
             length == loc.length &&
             width == loc.width;
    }
  }
}
