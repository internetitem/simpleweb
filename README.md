# SimpleWeb #

A very simple Java-based web framework.

At this point, this project is a toy for experimenting with building a very basic web framework.

## Implemented Ideas ##

 * Simple routing using a simple syntax
 * Controllers are just regular objects with methods that can take a number of different types of arguments
 * Routes direct requests to the controller and controller method

## Future Ideas ##

 * Learn more about how to use Ivy properly
 * More sample code and documentation

## Running ##

There are a few ways to run this framework:

 * As a Web Application - Use `HttpDispatcherServlet` as your servlet. Any servlet init params will be used as the starting point for the Params
 * Within Jetty - Use `SimpleWebHandler` as your Handler. You will need to pass a `Map<String, String>` into the constructor which is the basis for the Params
 * Using the built-in embedded Jetty webserver - Run `SimpleWebServer`. The initial Params are parsed from the command line, using anything that matches the format `key=value`.

## Controllers ##

Controllers implement the `ControllerBase` interface. Actions within a Controller are regular methods. Actions must return an object implementing the `Response` interface. By default, all methods returning a `Response` object are potential actions, however this can be altered by adding the `@ControllerOptions` annotation to the Controller class, setting `exposeAll=false` and then tagging the methods to be exposed with the `@WebAction` annotation. Controller can have Params injected when constructed (which is the combination of the Application's initialization parameters, the initial configuraiton's `params` element, and the Controller's `params' element).

Actions may take a number of possible parameters, and the values for these parameters are automatically populated based on the parameter's type:

 * `HttpServletRequest` - The request's `HttpServletRequest`
 * `HttpServletResponse` - the request's `HttpServletResponse`
 * `String` - The full path that was matched
 * `Map` - The current state of the Params (everything built up during the application's initialization, plus any new values from the processing of the route)

### Params ###

One of the central themes of this framework is that there is a "Parameter Map" ("Params" for short) that is created when the framework is first initialized, and built up (new items added, possibly overwriting previous ones) as different parts of the application are initialized and as routes are matched.

Params are built up (in order) from the following places:

 * Application initialization (as described above)
 * The initial configuration's `params` element (which is a JSON map)
 * The matched controller instance's params
 * A route's attributes (every key defined as part of a route)
 * Variables matched as part of a route (:id, :name, etc...)

## Configuration ##

The framework's initial configuration is loaded from the classpath, from a file named by the Param `config.file`, but if that isn't specified, it defaults to `/config.json`. If this file is not found, the bootstrap configuration in `/basic-config.json` is used. This initial configuration specifies the `configClass` (implementing the `Configuration` interface) to use for the next phase of intialization, as well as an optional `params` map which is added to Params. The `Configuration` object can have any values from `Params` injected (by name) automatically (with conversions to `Integer`/`int`, `Boolean`/`bool` if necessary). Additionally, if there is a property `params` with a single `Map` as its parameter (in other words a method `setParams(Map)`), the Params will be injected there.

### Sample `config.json` ###

    {
      configClass: 'com.internetitem.simpleweb.config.BasicConfiguration',
      params: {
        routes: 'routes.json'
      }
    }

The default `BasicConfiguration` expects that the `routes' Param be set to a filename (defaults to 'routes.json'). Any files with that name are loaded (in classpath order) to add routes.

### Routes ###

Each route maps a set of URLs to a named method ("action") within a controller. Each controller class is defined in the router's configuration, and is then referenced by name within routes.

Routes are checked in the order that they were defined, and the first matching route wins. All attributes of the route are added to Params for that request, followed by any labeled matches within the route.

The following Params have special meanings to the routing process, and are used as part of routing decisions:

 * `pattern` - The pattern to match (described below)
 * `controller` - The name of the controller to use (as defined in the `routes.json` file)
 * `invalid:controller` - A "backup" controller that will be used if the original `controller` is not found
 * `action` - The method to call within the controller (defaults to "index")
 * `invalid:action` - A "backup" method that will be used if the original `action` is not found
 * `redirect` - If set, an HTTP Redirect is sent to the client. Note that this parameter uses expansion and has access to the current state of Params

Note that the Params listed above must be defined within the route itself and aren't inherited as part of the usual Params process (although they will be available in the final Params passed to the controller's action method).

#### Sample `routes.json` #####

    {
      controllers: [
        {
          name: 'test',
          className: 'com.internetitem.simpleweb.test.TestController'
        },
        {
          name: 'files',
          className: 'com.internetitem.simpleweb.controllers.StaticFileController',
          params: {
            file: '/files/${path}',
            serveFrom: 'classpath'
          }
        }
      ],
      routes: [
        { pattern: '/test/?', controller: 'test', action: 'test1', which: '1' },
        { pattern: '/test/?', controller: 'test', action: 'test1', which: '2' }
      }
    }


#### Route Syntax ####

 * A route contains a number of "parts", each separated by slashes
 * Each part can be a string of characters or a wildcard (`*`)
 * Strings of characters:
   * May contain a label like `:id`
   * Any text matched in the place of the `:label` will be added to the Params
   * The part may end with a question mark (`?`) at the end to indicate that the part is optional. Note that once an optional part is found, all parts that follow are always optional
 * The wildcard (`*`):
   * May only occur once in a URL
   * The matched text is always optional
   * Any text captured is stored in the `path` Param
 * If the route ends with a slash (`/`), that slash may be followed by a question mark to indicate that the slash is optional

## Build-in Controllers ##

### StaticFileController ###

Serve static files from the classpath or filesystem.

Configured using the following Params:

 * `file` - The path to be served. This is expanded with full access to the current state of Params
 * `path` - If `file` is missing, this is used as the file to be served instead
 * `serveFrom` - If set to `classpath` then files are served from the classpath. Otherwise they are served from the filesystem
 * `contentType` - If this is set, it is used as the `Content-Type` for all files served. Otherwise the content type is automatically detected using the filename



