package com.movieweb.service;

import javax.servlet.http.Part;
import javax.servlet.ServletContext;

import com.movieweb.DAO.UsersDAO;
import com.movieweb.model.Users;

public class InformationChangeService
{
    private UsersDAO usersDAO;
    private ImageService imageService;
    public InformationChangeService(ServletContext servletContext)
    {
        usersDAO = new UsersDAO();
        imageService = new ImageService(servletContext);
    }
    public boolean updateInformation(
            Users currentUser,
            String fullName,
            String email,
            String phone, 
            Part avatarPart, 
            Part bannerPart)
    {
        if (currentUser == null)
        {
            return false;
        }

        /*Remove unnecessary spaces.*/
        fullName = fullName == null? "": fullName.trim();
        email = email == null? "": email.trim();
        phone = phone == null? "": phone.trim();

        /*Full name and email are required.*/
        if (fullName.isEmpty()
                || email.isEmpty())
        {
            return false;
        }

        /*Check whether the new email already belongs to another user.*/
        Users emailUser = usersDAO.getUserByEmail(email);
        if (emailUser != null
                && emailUser.getUserId()
                != currentUser.getUserId())
        {
            return false;
        }

        /*Phone is optional.
        Only check for duplicates when
        the user actually entered a phone.*/
        if (!phone.isEmpty())
        {
            Users phoneUser = usersDAO.getUserByPhone(phone);
            if (phoneUser != null
                    && phoneUser.getUserId()
                    != currentUser.getUserId())
            {
                return false;
            }
        }

        /*Avatar upload.*/
        String avatarPath = imageService.saveAvatar(
							                        currentUser,
							                        avatarPart);

        /*If an avatar was uploaded but could not be saved, stop the update.*/
        if (avatarPart != null && avatarPath == null)
        {
            return false;
        }

        /*Banner upload. */
        String bannerPath = imageService.saveBanner(
							                        currentUser,
							                        bannerPart);

        /*If a banner was uploaded but could not be saved, stop the update.*/
        if (bannerPart != null && bannerPath == null)
        {
            return false;
        }

        /*Update the database.*/
        boolean updated =
                usersDAO.updateUserInformation(
                        currentUser.getUserId(),
                        fullName,
                        email,
                        phone, 
                        avatarPath, 
                        bannerPath);
        if (!updated)
        {
            return false;
        }

        /*Update the Users object stored in the current session.*/
        currentUser.setFullName(fullName);
        currentUser.setEmail(email);
        currentUser.setPhone(
                phone.isEmpty()
                ? null
                : phone);
        /*Only replace the session paths when a new image was uploaded.*/
        if (avatarPath != null)
        {
            currentUser.setAvtPath(avatarPath);
        }

        if (bannerPath != null)
        {
            currentUser.setBannerPath(bannerPath);
        }
        return true;
    }
}
