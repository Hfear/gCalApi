
import React from 'react';
import ReactDOM from 'react-dom/client';
import {
    createBrowserRouter,
    RouterProvider,
    Navigate
} from "react-router-dom";

import Navbar from './Navbar';
import SignIn from './SignIn';
import HomePage from './HomePage';
import ProtectedRoute from "./ProtectedRoute.jsx";
import CreateClassForm from "./CreateClassForm.jsx";
import JoinClassForm from "./JoinClassForm.jsx";
import Profile, {loader as profileLoader} from "./Profile.jsx";
import MyClasses, {loader as myClassesLoader} from "./MyClasses.jsx";
import ClassPage, { loader as classPageLoader } from './ClassPage.jsx';
import GroupPage, { loader as groupLoader } from './GroupPage';
import MyGroups, { loader as myGroupsLoader } from './MyGroups.jsx';
import CreateGroupForm from './CreateGroupForm.jsx';
import JoinGroupForm from "./JoinGroupForm.jsx";
import AddEventForm from "./AddEventForm.jsx"

const router = createBrowserRouter([
    {
        path: '/',
        element: <Navbar />,
        children: [
            {
                index: true,
                element: <Navigate to="/signin" />
            },
            {
                path: 'signin',
                element: <SignIn />
            },
            {
                path: 'homepage',
                element: (<ProtectedRoute>
                    <HomePage />
                </ProtectedRoute>)
            },
            {
                path: 'create-classform',
                element: (<ProtectedRoute>
                    <CreateClassForm/>
                </ProtectedRoute>)
            },
            {
                path: 'join-classform',
                element: (<ProtectedRoute>
                    <JoinClassForm/>
                </ProtectedRoute>)
            },
            {
                path: 'profile',
                element: (<ProtectedRoute>
                    <Profile/>
                </ProtectedRoute>),
                loader: profileLoader,
            },
            {
                path: 'my-classes',
                element: (<ProtectedRoute>
                    <MyClasses/>
                </ProtectedRoute>),
                loader: myClassesLoader,
            },
            {
                path: 'class/:code',
                element: (
                    <ProtectedRoute>
                        <ClassPage/>
                    </ProtectedRoute>
                ),
                loader: classPageLoader
            },
            {
                path: 'class/:classCode/group/:groupCode',
                element: (
                    <ProtectedRoute>
                        <GroupPage />
                    </ProtectedRoute>
                ),
                loader: groupLoader
            },
            {
                path: 'my-groups',
                element: (
                    <ProtectedRoute>
                        <MyGroups />
                    </ProtectedRoute>
                ),
                loader: myGroupsLoader,
            },
            {
                path: 'class/:classCode/create-group',
                element: (
                    <ProtectedRoute>
                        <CreateGroupForm />
                    </ProtectedRoute>
                )
            },
            {
                path: 'class/:classCode/groups/:groupCode/join',
                element: (
                    <ProtectedRoute>
                        <JoinGroupForm />
                    </ProtectedRoute>
                )
            },
            {
                path: 'class/:classCode/groups/:groupCode/add-event',
                element: (
                    <ProtectedRoute>
                      <AddEventForm />
                    </ProtectedRoute>
                  )
              },
            {
                path: '*',
                element: <Navigate to="/signin" />
            }
        ]
    }
]);

ReactDOM.createRoot(document.getElementById('root')).render(
    <React.StrictMode>
        <RouterProvider router={router} />
    </React.StrictMode>
);
